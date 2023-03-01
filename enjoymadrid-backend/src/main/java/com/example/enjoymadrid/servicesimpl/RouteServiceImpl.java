package com.example.enjoymadrid.servicesimpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import com.example.enjoymadrid.models.AirQualityPoint;
import com.example.enjoymadrid.models.BicycleTransportPoint;
import com.example.enjoymadrid.models.Frequency;
import com.example.enjoymadrid.models.Point;
import com.example.enjoymadrid.models.PointWrapper;
import com.example.enjoymadrid.models.Polyline;
import com.example.enjoymadrid.models.PublicTransportLine;
import com.example.enjoymadrid.models.PublicTransportPoint;
import com.example.enjoymadrid.models.Route;
import com.example.enjoymadrid.models.Segment;
import com.example.enjoymadrid.models.Time;
import com.example.enjoymadrid.models.TransportPoint;
import com.example.enjoymadrid.models.User;
import com.example.enjoymadrid.models.dtos.RouteResultDto;
import com.example.enjoymadrid.models.repositories.AirQualityPointRepository;
import com.example.enjoymadrid.models.repositories.PublicTransportLineRepository;
import com.example.enjoymadrid.models.repositories.RouteRepository;
import com.example.enjoymadrid.models.repositories.TransportPointRepository;
import com.example.enjoymadrid.models.repositories.UserRepository;
import com.example.enjoymadrid.services.RouteService;
import com.example.enjoymadrid.services.SharedService;
import com.example.enjoymadrid.services.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import reactor.core.publisher.Mono;

@Service
public class RouteServiceImpl implements RouteService {
	
	// For heuristic, weighting factors
	private static final double PREFERENCE_FACTOR = 1.5;
	
	private final UserService userService;
	private final UserRepository userRepository;
	private final RouteRepository routeRepository;
	private final TransportPointRepository transportPointRepository;
	private final PublicTransportLineRepository publicTransportLineRepository;
	private final AirQualityPointRepository airQualityPointRepository;
	private final SharedService sharedService;
	
	public RouteServiceImpl(RouteRepository routeRepository, UserRepository userRepository, UserService userService, 
			TransportPointRepository transportPointRepository, PublicTransportLineRepository publicTransportLineRepository, 
			AirQualityPointRepository airQualityPointRepository, SharedService sharedService) {
		this.routeRepository = routeRepository;
		this.userRepository = userRepository;
		this.userService = userService;
		this.transportPointRepository = transportPointRepository;
		this.publicTransportLineRepository = publicTransportLineRepository;
		this.airQualityPointRepository = airQualityPointRepository;
		this.sharedService = sharedService;
	}

	@Override
	public List<Route> getUserRoutes(Long userId) {
		// Get user's routes from DB
		User user = this.userService.getUser(userId);
		return user.getRoutes();
	}
	
	@Override
	public void deleteRoute(Long routeId, Long userId) {
		// Get user (delete route from user's routes) & route to be deleted in DB
		Route route = this.routeRepository.findById(routeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ruta no encontrada"));
		User user = this.userService.getUser(userId);
		user.getRoutes().remove(route);
		this.userRepository.save(user);
		this.routeRepository.delete(route);
	}
	
	@Override
	public RouteResultDto createRoute(Route route, Long userId) {
		// Parameters to create route
		TransportPoint origin = route.getOrigin();
		TransportPoint destination = route.getDestination();
		origin.setType("");
		origin.setNearbyTouristicPoints(new HashMap<>());
		destination.setType("");
		
		// Walking distance to the next transport point
		double maxDistance = route.getMaxDistance() * 0.7;
		
		// User's interests
		Map<String, Integer> preferences = route.getPreferences();
		
		// Map with lines of the public transport stops
		Map<String, PublicTransportLine> lineStops = this.publicTransportLineRepository.findAll().stream()
				.collect(Collectors.toMap(line -> 
					line.getTransportType() + "_" + line.getLine() + " [" + line.getDirection() + "]", 
					line -> line));
		
		// Get all the transport points selected by user
		List<TransportPoint> transportPoints = getTransportPoints(route.getTransports(), lineStops);
		
		// Get the air quality measuring stations (that AQI levels are currently available)
		List<AirQualityPoint> airQualityPoints = this.airQualityPointRepository.findByAqiIsNotNull();
				
		// Get only bicycle stations from DB if selected by user
		Set<TransportPoint> bicyclePoints = route.getTransports().contains("BiciMAD") ? 
				transportPoints.stream()
					.filter(stop -> stop instanceof BicycleTransportPoint)
					.collect(Collectors.toSet()) 
				: new HashSet<>();
		
		// Find the best route
		List<TransportPoint> routePoints = findBestRoute(origin, destination, maxDistance, transportPoints, 
				preferences, airQualityPoints, bicyclePoints);
		if (routePoints == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"No se ha podido crear la ruta con estos parámetros de entrada");
		}
					
		// Set the different segments that form the route
		RouteResultDto routeResultDto = setSegments(routePoints, route.getName(), lineStops);
		
		// If user logged in store route in DB
		if (userId != null && route.getId() == null) {
			User user = this.userService.getUser(userId);
			route = this.routeRepository.save(route);
			user.getRoutes().add(route);
			this.userRepository.save(user);
			routeResultDto.setId(route.getId());
		}
		
		return routeResultDto;
	}
			
	/**
	 * Find the best route using A* algorithm
	 * 
	 * @param <P> Type inference
	 * @param origin Origin point of route
	 * @param destination Destination point of route
	 * @param maxDistance Maximum distance to walk between points of route
	 * @param transportPoints Transport points filtered by user's chosen transport mode
	 * @param preferences User's tourist preferences
	 * @return Complete route
	 */
	private <P extends Comparable<P>> List<P> findBestRoute(P origin, P destination, double maxDistance,
			List<P> transportPoints, Map<String, Integer> preferences, List<AirQualityPoint> airQualityPoints,
			Set<P> bicyclePoints) {

		// Map that delivers the wrapper for a point
		Map<P, PointWrapper<P>> points = new HashMap<>();
		// Iterate over the points ordered by best cost
		TreeSet<PointWrapper<P>> openList = new TreeSet<>();
		// Check if a point has already been processed
		Set<P> bestPointsFound = new HashSet<>();

		// Add origin point
		PointWrapper<P> originWrapper = new PointWrapper<>(origin, null, false, 0.0,
				calculateHeuristic(origin, destination, airQualityPoints, preferences));
		points.put(origin, originWrapper);
		openList.add(originWrapper);

		while (!openList.isEmpty()) {
			PointWrapper<P> pointWrapper = openList.pollFirst();
			P point = pointWrapper.getPoint();
			bestPointsFound.add(point);

			// Point destination reached, return list of points
			if (calculateDistance(point, destination) <= maxDistance
					&& (isDirectNeighbor(pointWrapper.getPrevious() != null ? pointWrapper.getPrevious().getPoint() : null, point, true) 
						|| point.equals(origin))) {
				List<P> route = new LinkedList<>();
				while (pointWrapper != null) {
					route.add(0, pointWrapper.getPoint());
					pointWrapper = pointWrapper.getPrevious();
				}
				route.add(destination);
				return route;
			}

			Set<P> neighbors = getNeighbors(pointWrapper, point, transportPoints, bicyclePoints, maxDistance);			
			for (P neighbor : neighbors) {
				// Continue with next neighbor if already in best points
				if (bestPointsFound.contains(neighbor)) {
					continue;
				}

				// Calculate cost from start to neighbor via current node
				double cost = calculateDistance(point, neighbor);
				double distanceFromOrigin = pointWrapper.getDistanceFromOrigin() + cost;				
				boolean directNeighbor = isDirectNeighbor(point, neighbor, false);

				// Neighbor not discovered yet
				PointWrapper<P> neighborWrapper = points.get(neighbor);
				if (neighborWrapper == null) {
					neighborWrapper = new PointWrapper<P>(neighbor, pointWrapper, directNeighbor, distanceFromOrigin,
							calculateHeuristic(neighbor, destination, airQualityPoints, preferences));
					points.put(neighbor, neighborWrapper);
					openList.add(neighborWrapper);
				}
				// Neighbor discovered, but total cost via current node is lower -> Update costs
				// & previous point
				else if (distanceFromOrigin < neighborWrapper.getDistanceFromOrigin()) {
					openList.remove(neighborWrapper);
					// Modify Point of PointWrapper in case lines have changed (subway, commuter & bus)
					neighborWrapper.setPoint(neighbor);
					// Update previous point, distance from start & if it's a direct neighbor
					neighborWrapper.setDirectNeighbor(directNeighbor);
					neighborWrapper.setDistanceFromOrigin(distanceFromOrigin);
					neighborWrapper.setPrevious(pointWrapper);
					openList.add(neighborWrapper);
				}

			}

		}
				
		return null;
	}
	
	/**
	 * Get neighboring points to actual point
	 * 
	 * @param <P> Type Inference
	 * @param pointWrapper Wrapper of point with info of previous point or estimated cost to destination
	 * @param point Info of current point
	 * @param transportPoints Selected transports by user
	 * @param bicyclePoints Set with all the bicycle stations
	 * @param maxDistance Maximum distance to walk between points
	 * @return Set of neighboring points to current point
	 */
	@SuppressWarnings("unchecked")
	private <P extends Comparable<P>> Set<P> getNeighbors(PointWrapper<P> pointWrapper, P point, List<P> transportPoints, 
			Set<P> bicyclePoints, double maxDistance) {
		// No duplicates in neighbors
		Set<P> neighbors = new HashSet<>();	
		
		// Check if previous point and actual point are directly connected (Example: Same line of bus or subway)
		boolean directNeighbors = isDirectNeighbor(pointWrapper.getPrevious() != null ? pointWrapper.getPrevious().getPoint() : null, point, true);
		if (point instanceof PublicTransportPoint) {	
			// If public transport get next stop in line(s)
			neighbors =  ((PublicTransportPoint) point).getNextStops()
					.values().stream()
					.map(stop -> (P) stop)
					.collect(Collectors.toSet());		
		} else if (point instanceof BicycleTransportPoint) {			
			// If bicycle station get available stations 
			neighbors = new HashSet<>(bicyclePoints);
		}
		
		if (directNeighbors || (neighbors.isEmpty() && !(point instanceof PublicTransportPoint))) {
			// Get lines of point if it's public transport stop
			Set<String> linesPoint = point instanceof PublicTransportPoint ? 
					((PublicTransportPoint) point).getStopLines().stream()
					.map(line -> line[0] + " [" + line[1] + "]")
					.collect(Collectors.toSet())
					: new HashSet<>();
			
			neighbors.addAll(
				// Iterate over points (nearest distance established by user)
				// & if stop is public transport delete from stops same lines
				// & add only stops if not already in neighbors Set
				transportPoints.stream()					
					.filter(neighbor -> calculateDistance(point, neighbor) <= maxDistance)
					.map(neighbor -> {
						if (neighbor instanceof PublicTransportPoint 
								&& (((PublicTransportPoint) neighbor).getType().equals(((TransportPoint) point).getType()))) {
							PublicTransportPoint neighborCopy = new PublicTransportPoint((PublicTransportPoint) neighbor);
							Set<String[]> linesNeighbor = neighborCopy.getStopLines().stream()
									.filter(line -> linesPoint.contains(line[0] + " [" + line[1] + "]"))
									.collect(Collectors.toSet());
							linesNeighbor.forEach(line -> {
								neighborCopy.getStopLines().remove(line);
								neighborCopy.getNextStops().remove(line[0] + " [" + line[1] + "]");
							});
							return (P) neighborCopy;
						}
						return neighbor;
					})
					.filter(neighbor -> {
						if (neighbor instanceof PublicTransportPoint 
								&& ((PublicTransportPoint) neighbor).getStopLines().isEmpty()) {
							return false;
						}
						return true;
					})
					.collect(Collectors.toSet())
			);
		}
				
		return neighbors;
	}
	
	/**
	 * Points are directly connected
	 * 
	 * @param <P> Type inference
	 * @param previous Previous point of actual in A* algorithm
	 * @param point Info of point
	 * @param includeBicycle If bicycle stations are considered
	 * @return If previous point & actual point are neighbors (e.g. same subway/bus line & directly connected)
	 */
	private <P extends Comparable<P>> boolean isDirectNeighbor(P previous, P point, boolean includeBicycle) {
		
		if (previous != null && point != null) {
			if (previous instanceof PublicTransportPoint && point instanceof PublicTransportPoint) {
				return ((PublicTransportPoint) previous).getNextStops().values().contains((PublicTransportPoint) point);
			} else if (includeBicycle && previous instanceof BicycleTransportPoint 
					&& point instanceof BicycleTransportPoint) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * Get heuristic from current point to destination
	 * 
	 * @param <P> Type inference
	 * @param point Info point
	 * @param destination Destination point
	 * @param airQualityPoints Air quality stations in Madrid
	 * @param touristicPoints Tourist places in Madrid
	 * @param preferences User preferences
	 * @return Heuristic
	 */
	private <P extends Comparable<P>> double calculateHeuristic(P point, P destination,
			List<AirQualityPoint> airQualityPoints, Map<String, Integer> preferences) {
						
		// Calculate distance to destination using Haversine formula
		double minDistanceToDestination = calculateDistance(point, destination);

		// Get air quality level from nearest station
		int aqi = airQualityPoints.stream()
				.min(Comparator.comparingDouble(station -> this.sharedService.haversine(station.getLatitude(), station.getLongitude(), 
						((Point) point).getLatitude(), ((Point) point).getLongitude())))
				.map(AirQualityPoint::getAqi)
				.orElse(1);
		
		// Number of nearby tourist points to station
		Map<String, Long> nearbyTouristicPoints = ((TransportPoint) point).getNearbyTouristicPoints();
		double interestPlaces = preferences.entrySet().stream().reduce(0.0, (sum, preference) -> {	
			double nearbyPlaces = nearbyTouristicPoints.getOrDefault(preference.getKey(), 0L);
			nearbyPlaces *= preference.getValue() * PREFERENCE_FACTOR; 
			return sum + nearbyPlaces;
		}, Double::sum);
		
		// Ensure non-zero values
		aqi = Math.max(aqi, 1);
		interestPlaces = Math.max(interestPlaces, 1.0);
		
		return (minDistanceToDestination * aqi) / interestPlaces;
	}
		
	/**
	 * Calculate haversine formula via latitude & longitude
	 * 
	 * @param <P> Type inference
	 * @param origin Source point
	 * @param destination Target point
	 * @return Haversine formula
	 */
	private <P extends Comparable<P>> double calculateDistance(P origin, P destination) {
		return this.sharedService.haversine(((Point) origin).getLatitude(), ((Point) origin).getLongitude(), 
				((Point) destination).getLatitude(), ((Point) destination).getLongitude());
	}
	
	/**
	 * Get transport points (subway, bus, biciMAD, Commuter) selected by user & filter by current time or station availability
	 * 
	 * @param transports Transport methods
	 * @param lineStops Bus, underground & suburban lines
	 * @return Transport points
	 */
	private List<TransportPoint> getTransportPoints(List<String> transports, Map<String, PublicTransportLine> lineStops) {
		
		// Get actual time & day of week
		LocalTime currentLocalTime = ZonedDateTime.now(ZoneId.of("Europe/Madrid")).toLocalTime();
		DayOfWeek currentDayOfWeek = ZonedDateTime.now(ZoneId.of("Europe/Madrid")).toLocalDate().getDayOfWeek();
				
		// Query to get the points in order to create the route
		List<TransportPoint> transportPoints = this.transportPointRepository.findByTypeIn(transports);
						
		return transportPoints.stream()
				.map(point -> {
					if (point instanceof PublicTransportPoint) {						
						PublicTransportPoint publicTransportPoint = (PublicTransportPoint) point;
						Iterator<String[]> linesIterator = publicTransportPoint.getStopLines().iterator();
						while (linesIterator.hasNext()) {
							String[] line = (String[]) linesIterator.next();
							PublicTransportLine publicTransportLine = lineStops.get(point.getType() + "_" + line[0] + " [" + line[1] + "]");
							
							// Schedule is null & stop doesn't operate right now
							boolean scheduleNull = false;
							// First/last time operates the station
							LocalTime startTime = LocalTime.MIDNIGHT;
							LocalTime endTime = LocalTime.MIDNIGHT;
							// Schedule is frequency or arrival times
							if (publicTransportLine.getScheduleType().equals('F')) {
								Frequency frequency = (Frequency) publicTransportLine.getStopSchedules().get(currentDayOfWeek.toString());
								if (frequency == null) {
									scheduleNull = true;
								} else {
									startTime = frequency.getStartSchedule();
									endTime = frequency.getEndSchedule();
								}
							} else {
								Time time = (Time) publicTransportLine.getStopSchedules().get(line[2]);
								if (time == null) {
									scheduleNull = true;
								} else {
									LocalTime[] arrivalTimes = time.getDayTimes().get(currentDayOfWeek.toString());
									startTime = arrivalTimes[0];
									endTime = arrivalTimes[arrivalTimes.length - 1];	
								}
							}
							
							// Different approach depending on whether the end time is after or before midnight
							if ( scheduleNull || ( startTime.isBefore(endTime) && ( currentLocalTime.isBefore(startTime) || currentLocalTime.isAfter(endTime) ) ) 
									|| ( startTime.isAfter(endTime) && (currentLocalTime.isBefore(startTime) && currentLocalTime.isAfter(endTime) ) ) ) {
								linesIterator.remove();
								publicTransportPoint.getNextStops().remove(line[0] + " [" + line[1] + "]");
							}
							
						}
					}
					return point;
				}).filter(point -> {
					if (point instanceof PublicTransportPoint && ((PublicTransportPoint) point).getStopLines().isEmpty()) {
						// Remove public transport stop if there aren't any line that operate currently
						return false;
					} else if (point instanceof BicycleTransportPoint 
							&& !((BicycleTransportPoint) point).isAvailable()) {
						// Exclude bike stations that don't operate (currently) nor have bikes available
						return false;
					}
					return true;
				})
				.collect(Collectors.toList());
	}
	
	/**
	 * Create the segments that make up the route (e.g. get coordinates, duration, distance...)
	 * 
	 * @param routePoints Points that make up the route
	 * @param name Name of route
	 * @param lineStops Lines of the public transport stops
	 * @return Formed route
	 */
	private RouteResultDto setSegments(List<TransportPoint> routePoints, String name, Map<String, PublicTransportLine> lineStops) {
		
		// List of Segments that create the route
		List<Segment> routeSegments = new ArrayList<>();
		
		// Associate the mode of transport
		Map<String, String> modeTransports = Map.of(
				"A pie", "foot-walking",
				"BiciMAD", "cycling-electric",
				"Bus", "driving-car");
		// Polyline color for walk & bike
		Map<String, String> polylineColors = Map.of(
				"A pie", "#2D2E2D",
				"BiciMAD", "#FFAD00");
		// Get mode of transport
		String transportMode = "";
		
		// Distance & duration of the total route
		double duration = 0.0;
		
		// Get actual time & day of week
		LocalTime currentLocalTime = ZonedDateTime.now(ZoneId.of("Europe/Madrid")).toLocalTime();
		DayOfWeek currentDayOfWeek = ZonedDateTime.now(ZoneId.of("Europe/Madrid")).toLocalDate().getDayOfWeek();
		
		// Return a route between two or more locations for a selected profile
		WebClient client = WebClient.create("https://api.openrouteservice.org");
		//WebClient client = WebClient.create("http://localhost:8088/ors");
		for (int i = 0; i < routePoints.size(); i++) {	
			
			// Index of first of segment
			Integer source = i;
			
			// Add coordinates
			List<TransportPoint> points = new ArrayList<>();
			// List to differentiate the different public transport lines on the route
			List<String> linesList = new ArrayList<>();
			for (int j = i; j < routePoints.size(); j++) {
				// Get point
				TransportPoint transportPoint_1 = routePoints.get(j);
				// Get next point to current
				TransportPoint transportPoint_2 = routePoints.get(j + 1);
				
				// Add coordinates first point
				points.add(transportPoint_1);
								
				// Check if same mode of transport
				if (transportPoint_1.getType().equals(transportPoint_2.getType())) {
					// Subway, commuter or bus
					if (transportPoint_1 instanceof PublicTransportPoint && transportPoint_2 instanceof PublicTransportPoint) {
						// Cast to Public Transport Point
						PublicTransportPoint publicTransportPoint_1 = (PublicTransportPoint) transportPoint_1;
						PublicTransportPoint publicTransportPoint_2 = (PublicTransportPoint) transportPoint_2;
						if (publicTransportPoint_1.getNextStops().containsValue(publicTransportPoint_2)) {
							// Get lines in common between the two points
							List<String> nextLines = publicTransportPoint_1.getNextStops().entrySet().stream()
									.filter(entry -> entry.getValue().equals(publicTransportPoint_2))
									.map(Map.Entry::getKey)
									.collect(Collectors.toList());
							// First iteration
							if (linesList.isEmpty()) {
								linesList = nextLines;
							}
							// Check if transfer between stop to different line or similar is done
							nextLines.retainAll(linesList);
							if (!nextLines.isEmpty() ) {
								linesList = nextLines;
								continue;
							}
							
						}
					}
					// Bicycle
					else if (transportPoint_1 instanceof BicycleTransportPoint && transportPoint_2 instanceof BicycleTransportPoint) {
						continue;
					}
				}
								
				// 2 or more points of the same type (in public tranport also same line)
				if (points.size() >= 2) {
					// Reduce index and then we calculate the segment
					transportMode = transportPoint_1.getType();
					i = j - 1;
					break;
				}
				
				// Add coordinates second point & update index
				// Segment has to be done by walking
				transportMode = "A pie";
				points.add(transportPoint_2);
				// Last point, update index and calculate segment
				if ((j + 1) == (routePoints.size() - 1)) {
					i = j + 1;
				} else { // Update index and create segment
					i = j;
				}
				break;
			}
			
			// Index of first & last point of segment
			Integer target = source + points.size() - 1;
			
			// Create segment 
			Segment segment = new Segment(source, target, transportMode);
			
			// Store the coordinates that form the polyline
			List<Double[]> polylineList = new ArrayList<>();
			
			// Duration of the segment
			double durationSegment = 0.0;
			
			if (modeTransports.containsKey(transportMode)) {
				// Transform the points (longitude/latitude) into their coordinates
				StringBuilder coordinates = new StringBuilder();
				Iterator<TransportPoint> iterator = points.iterator();
				while (iterator.hasNext()) {
					TransportPoint point = iterator.next();
					coordinates.append("[" + point.getLongitude() + "," + point.getLatitude() + "]");
					if (iterator.hasNext()) 
						coordinates.append(",");
				}
				
				// Get response
				ObjectNode response = client.post()
						.uri("/v2/directions/" + modeTransports.get(transportMode) + "/geojson")
						.header(HttpHeaders.AUTHORIZATION, "5b3ce3597851110001cf6248079a826553c748d0aed309710623ce33")
						.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE, "application/geo+json")
						.contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(
								"{\"coordinates\":[" + coordinates + "],"
								+ "\"language\":\"es-es\"}"
								+ "\"preference\":\"shortest\""))
						.retrieve()
						.bodyToMono(ObjectNode.class)
						.onErrorResume(WebClientResponseException.class, error -> Mono.empty())
						.block();
				
				// Distance of the segment
				double distanceSegment = 0.0;
				if (response != null) {
					// Get data of the response
					JsonNode features = response.get("features");
					JsonNode properties = features.findValue("properties");	
					
					// Get the points to draw the route
					JsonNode polyline = features.findValue("geometry").findValue("coordinates");
					for (JsonNode coordinatesNode: polyline) {
						Double longitude = coordinatesNode.get(0).asDouble();
						Double latitude = coordinatesNode.get(1).asDouble();
						polylineList.add(new Double[] {latitude, longitude});
					}
					
					// Get duration
					durationSegment = properties.get("summary").get("duration").asDouble();
									
					// For bicycle & walk, the steps & distance of segment
					if (!transportMode.equals("Bus")) {	
						// Get distance
						distanceSegment = properties.get("summary").get("distance").asDouble() / 1000;
						
						// Handle the steps
						List<String> stepsMap = new ArrayList<>();
						List<JsonNode> stepsList = properties.get("segments").findValues("steps");
						for (JsonNode steps : stepsList) {
							for (JsonNode step : steps) {
								JsonNode way_points = step.get("way_points");
								Integer first = way_points.get(0).asInt();
								Integer last = way_points.get(1).asInt();
								String instruction = step.get("instruction").asText();
								stepsMap.add(first + "-" + last + ":" + instruction);
							}
						}
						segment.setSteps(stepsMap);
					}
				} else {
					polylineList.addAll(points.stream()
							.map(point -> new Double[] {point.getLatitude(), point.getLongitude()})
							.collect(Collectors.toList())
					);
					Point sourcePoint = points.get(0);
					Point targetPoint = points.get(points.size() - 1);
					// Calculate distance using haversine formula 
					distanceSegment = this.sharedService.haversine(sourcePoint.getLatitude(), sourcePoint.getLongitude(),
							targetPoint.getLatitude(), targetPoint.getLongitude());
					// Get time using duration = distance / speed
					double speed = transportMode.equals("A pie") ? 6.0 // Walking
							: transportMode.equals("BiciMAD") ? 15.71 // BiciMAD
							: 13.5; // EMT Bus
					durationSegment = (double) (distanceSegment / speed) * 3600;	
				}
				
				// Adjust distance, add to segment
				distanceSegment = BigDecimal.valueOf(distanceSegment).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
				segment.setDistance(distanceSegment);
			}
			
			// Color of polyline for the segment
			String color;
			
			if (!linesList.isEmpty()) {
				PublicTransportLine publicTransportLine = lineStops.get(transportMode + "_" + linesList.get(0));
				String line = publicTransportLine.getLine();
				String direction = publicTransportLine.getDirection();
				String destination = publicTransportLine.getDestination();
				color = publicTransportLine.getColor();
								
				// Type of schedule (frequency/arrival times)
				char typeSchedule = publicTransportLine.getScheduleType();
				if (typeSchedule == 'F') {
					Frequency frequency = (Frequency) publicTransportLine.getStopSchedules().get(currentDayOfWeek.toString());
					for (Entry<String, Integer> frequencyRanges: frequency.getDayFrequencies().entrySet()) {
						String[] frequencyTimes = frequencyRanges.getKey().split("-");
						LocalTime startFrequency = LocalTime.parse(frequencyTimes[0]);
						LocalTime endFrequency = LocalTime.parse(frequencyTimes[1]);
						if ( ( startFrequency.isBefore(endFrequency) && ( currentLocalTime.isAfter(startFrequency) && currentLocalTime.isBefore(endFrequency) ) ) 
								|| ( startFrequency.isAfter(endFrequency) && ( currentLocalTime.isAfter(startFrequency) || currentLocalTime.isBefore(endFrequency) ) ) ) {
							durationSegment += frequencyRanges.getValue();
							break;
						}
					}
				} else {
					String orderLine = ((PublicTransportPoint) points.get(0)).getStopLines().stream()
							.filter(lineStop -> lineStop[0].equals(line) && lineStop[1].equals(direction))
							.map(lineStop -> lineStop[2])
							.findFirst()
							.get();
					Time time = (Time) publicTransportLine.getStopSchedules().get(orderLine); 
					LocalTime[] arrivalTimes = time.getDayTimes().get(currentDayOfWeek.toString());
					durationSegment += Arrays.stream(arrivalTimes)
							.filter(arrivalTime -> arrivalTime.isAfter(currentLocalTime))
							.map(arrivalTime -> ChronoUnit.SECONDS.between(currentLocalTime, arrivalTime))
							.min(Long::compareTo).get();
				}
				
				// For public transport stops, except bus add the corresponding info
				for (int k = 0; k < points.size(); k++) {
					if (k == 0) continue;
					int orderLine = ((PublicTransportPoint) points.get(k)).getStopLines().stream()
							.filter(lineStop -> lineStop[0].equals(line) && lineStop[1].equals(direction))
							.map(lineStop -> Integer.parseInt(lineStop[2]))
							.findFirst()
							.get();
					Polyline polylineStop = publicTransportLine.getStopPolylines().get(orderLine);
					
					if (polylineStop != null) {
						durationSegment += polylineStop.getDuration();
						polylineList.addAll(polylineStop.getCoordinates());
					}
				}
				
				// Add estimated time taken between stops (30 seconds per stop)
				if (points.size() > 2) durationSegment += 30 * (points.size() - 2);
				
				// Add attributes to the segment
				segment.setLine(line);
				segment.setDestination(destination);
				
			} else {
				color = polylineColors.get(transportMode);
			}
			
			// Adjust duration, add to route general
			durationSegment = Math.round(durationSegment / 60);
			duration += durationSegment;
			
			// Add attributes to the segment
			segment.setPolyline(polylineList);
			segment.setDuration(durationSegment);
			segment.setColor(color);
						
			// Add segment to the rest of the segments of the route
			routeSegments.add(segment);
			
		}
				
		return new RouteResultDto(name, duration, routePoints, routeSegments);
	}

}
