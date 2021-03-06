package com.example.enjoymadrid.serviceslogic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.example.enjoymadrid.models.repositories.AirQualityPointRepository;
import com.example.enjoymadrid.models.repositories.PublicTransportLineRepository;
import com.example.enjoymadrid.models.repositories.RouteRepository;
import com.example.enjoymadrid.models.repositories.TouristicPointRepository;
import com.example.enjoymadrid.models.repositories.TransportPointRepository;
import com.example.enjoymadrid.models.repositories.UserRepository;
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
import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.TransportPoint;
import com.example.enjoymadrid.models.User;
import com.example.enjoymadrid.models.dtos.RouteResultDto;
import com.example.enjoymadrid.services.RouteService;
import com.example.enjoymadrid.services.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class RouteServiceLogic implements RouteService {
	
	private final UserService userService;
	private final UserRepository userRepository;
	private final RouteRepository routeRepository;
	private final TransportPointRepository transportPointRepository;
	private final PublicTransportLineRepository publicTransportLineRepository;
	private final TouristicPointRepository touristicPointRepository;
	private final AirQualityPointRepository airQualityPointRepository;
	
	public RouteServiceLogic(RouteRepository routeRepository, UserRepository userRepository, UserService userService, 
			TransportPointRepository transportPointRepository, PublicTransportLineRepository publicTransportLineRepository, 
			TouristicPointRepository touristicPointRepository, AirQualityPointRepository airQualityPointRepository) {
		this.routeRepository = routeRepository;
		this.userRepository = userRepository;
		this.userService = userService;
		this.transportPointRepository = transportPointRepository;
		this.publicTransportLineRepository = publicTransportLineRepository;
		this.touristicPointRepository = touristicPointRepository;
		this.airQualityPointRepository = airQualityPointRepository;
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
		destination.setType("");
		// Walking distance to the next transport point
		Double maxDistance = route.getMaxDistance() * 0.7;
		// User's interests
		Map<String, Integer> preferences = route.getPreferences();
		
		// Map with lines of the public transport stops
		Map<String, PublicTransportLine> lineStops = this.publicTransportLineRepository.findAll().stream()
				.collect(Collectors.toMap(line -> line.getTransportType() + "_" + line.getLine() + " [" + line.getDirection() + "]", line -> line));
		// Get all the transport points selected by user
		List<TransportPoint> transportPoints = getTransportPoints(route.getTransports(), lineStops);
		
		List<TransportPoint> routePoints = findBestRoute(origin, destination, maxDistance, transportPoints, preferences);
		if (routePoints == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST , "No se ha podido crear la ruta con estos par??metros de entrada");
		}
						
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
			
	private <P extends Comparable<P>> List<P> findBestRoute(P origin, P destination, Double maxDistance,
			List<P> transportPoints, Map<String, Integer> preferences) {

		// Map that delivers the wrapper for a point
		Map<P, PointWrapper<P>> points = new HashMap<>();
		// Iterate over the points ordered by best cost
		TreeSet<PointWrapper<P>> openList = new TreeSet<>();
		// Check if a point has already been processed
		Set<P> bestPointsFound = new HashSet<>();

		// Get the air quality measuring stations (that AQI levels are currently available)
		List<AirQualityPoint> airQualityPoints = this.airQualityPointRepository.findByAqiIsNotNull();
		// Get all the touristic points
		List<TouristicPoint> touristicPoints = this.touristicPointRepository.findAll();
		// Get only bicycle stations from DB if selected by user
		Set<P> bicyclePoints = transportPoints.stream()
				.filter(stop -> stop instanceof BicycleTransportPoint)
				.collect(Collectors.toSet());

		// Add origin point
		PointWrapper<P> originWrapper = new PointWrapper<>(origin, null, false, 0.0,
				calculateHeuristic(origin, destination, airQualityPoints, touristicPoints, preferences));
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
							calculateHeuristic(neighbor, destination, airQualityPoints, touristicPoints, preferences));
					points.put(neighbor, neighborWrapper);
					openList.add(neighborWrapper);
				}
				// Neighbor discovered, but total cost via current node is lower -> Update costs
				// & previous point
				else if (distanceFromOrigin < neighborWrapper.getDistanceFromOrigin()) {
					openList.remove(neighborWrapper);
					// Modify Point of PointWrapper in case lines have changed (subway, commuter & bus)
					neighborWrapper.setPoint(neighbor);
					// Update previous point, distance from start & if is a direct neighbor
					neighborWrapper.setDirectNeighbor(directNeighbor);
					neighborWrapper.setDistanceFromOrigin(distanceFromOrigin);
					neighborWrapper.setPrevious(pointWrapper);
					openList.add(neighborWrapper);
				}

			}

		}
				
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private <P extends Comparable<P>> Set<P> getNeighbors(PointWrapper<P> pointWrapper, P point, List<P> transportPoints, 
			Set<P> bicyclePoints, Double maxDistance) {
		// No duplicates in neighbors
		Set<P> neighbors = new HashSet<>();	
		
		// Check if previous point and actual point are directly connected (Example: Same line of bus or subway)
		boolean directNeighbors = isDirectNeighbor(pointWrapper.getPrevious() != null ? pointWrapper.getPrevious().getPoint() : null, point, true);
		if (point instanceof PublicTransportPoint) {	
			// If public transport get next stop in line(s)
			neighbors =  (Set<P>) ((PublicTransportPoint) point).getNextStops().values().stream().collect(Collectors.toSet());		
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
	
	private <P extends Comparable<P>> double calculateHeuristic(P point, P destination,
			List<AirQualityPoint> airQualityPoints, List<TouristicPoint> touristicPoints,
			Map<String, Integer> preferences) {
				
		// Calculate distance to destination using haversine formula
		double minDistanceToDestination = calculateDistance(point, destination);

		// Get air quality level from nearest station
		AirQualityPoint aqiStation = Collections.min(airQualityPoints, Comparator.comparing(station -> 
			haversine(station.getLatitude(), station.getLongitude(), ((Point) point).getLatitude(), ((Point) point).getLongitude())));
		int aqi = aqiStation != null ? aqiStation.getAqi() : 1;

		// Get touristic points within a radius of 500 meters
		List<TouristicPoint> nearTouristicPoints = touristicPoints.stream()
				.filter(touristicPoint -> haversine(touristicPoint.getLatitude(), touristicPoint.getLongitude(),
						((Point) point).getLatitude(), ((Point) point).getLongitude()) <= 0.5)
				.collect(Collectors.toList());
		
		double interestPlaces = 0.0;
		if (!nearTouristicPoints.isEmpty()) {
			// Calculate value respect to the number of sites of a given type
			interestPlaces = preferences.entrySet().stream().reduce(0.0, (sum, preference) -> {
				// Get preference type
				String preferenceName = preference.getKey().substring(preference.getKey().indexOf('_') + 1);
				// Search the touristic point by category attribute
				double nearPlaces = 0.0;
				if (preference.getKey().contains("C_")) {
					nearPlaces = nearTouristicPoints.stream()
							.filter(place -> place.getCategories().contains(preferenceName))
							.count();
				}
				// Preference is a combination of 2 types
				else if (preference.getKey().contains("R_")) {
					nearPlaces = nearTouristicPoints.stream()
							.filter(place -> place.getType().equals("Restaurantes") || place.getType().equals("Clubs"))
							.count();
				}
				// Preference by sport type or category
				else if (preference.getKey().contains("D_")) {
					nearPlaces = nearTouristicPoints.stream()
							.filter(place -> place.getType().equals(preferenceName)
									|| place.getCategories().contains("Instalaciones deportivas"))
							.count();
				}
				// Search the touristic point by type attribute
				else if (preference.getKey().contains("T_")) {
					nearPlaces = nearTouristicPoints.stream()
							.filter(place -> place.getType().equals(preferenceName))
							.count();
				}
				nearPlaces *= preference.getValue() * 1.5; 
				return sum + nearPlaces;
			}, (p1, p2) -> p1 + p2);
		}
		
		if (aqi == 0) aqi = 1;
		if (interestPlaces == 0.0) interestPlaces = 1.0;
		
		return (minDistanceToDestination * aqi) / interestPlaces;
	}
		
	private <P extends Comparable<P>> double calculateDistance(P origin, P destination) {
		Point source = (Point) origin;
		Point target = (Point) destination;
		return haversine(source.getLatitude(), source.getLongitude(), target.getLatitude(), target.getLongitude());
	}
	
	/**
	 * Calculate distance between two points on Earth
	 * @param lat1/lon1 start point latitude/longitude
	 * @param lat2/lat2 end point latitude/longitude
	 * @return Distance in kilometers
	 */
	private double haversine(double lat1, double lon1, double lat2, double lon2) {
		// Radius of earth (km)
		final double R = 6371; 
		
		// Distance between latitudes and longitudes
		double distLat = Math.toRadians(lat2 - lat1);
		double distLon = Math.toRadians(lon2 - lon1);
		
		// Convert latitudes to radians
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);
		
		// Haversine formula
		double h = Math.pow(Math.sin(distLat / 2), 2)
				+ Math.pow(Math.sin(distLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.asin(Math.sqrt(h)); //2 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h)); 
		
		return R * c;
	}
	
	private List<TransportPoint> getTransportPoints(List<String> transports, Map<String, PublicTransportLine> lineStops) {
		
		// Get actual time & day of week
		LocalTime currentLocalTime = ZonedDateTime.now(ZoneId.of("Europe/Madrid")).toLocalTime();
		DayOfWeek currentDayOfWeek = ZonedDateTime.now(ZoneId.of("Europe/Madrid")).toLocalDate().getDayOfWeek();
				
		// Query to get the points in order to create the route
		List<TransportPoint> transportPoints = this.transportPointRepository.findByTypeIn(transports);
						
		transportPoints = transportPoints.stream()
				.map(point -> {
					if (point instanceof PublicTransportPoint) {						
						PublicTransportPoint publicTransportPoint = (PublicTransportPoint) point;
						Set<String[]> lines = new HashSet<>(publicTransportPoint.getStopLines());
						for (String[] line: lines) {
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
							
							// Different approach depending on wether the end time is after or before midnight
							if ( scheduleNull || ( startTime.isBefore(endTime) && ( currentLocalTime.isBefore(startTime) || currentLocalTime.isAfter(endTime) ) ) 
									|| ( startTime.isAfter(endTime) && (currentLocalTime.isBefore(startTime) && currentLocalTime.isAfter(endTime) ) ) ) {
								publicTransportPoint.getStopLines().remove(line);
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
										
		return transportPoints;
	}
	
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
			// Distance & duration of the segment
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
						.block();
				
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
					double distanceSegment = properties.get("summary").get("distance").asDouble();
					// Adjust distance, add to segment
					distanceSegment = BigDecimal.valueOf(distanceSegment / 1000).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
					segment.setDistance(distanceSegment);
					
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
					for (var frequencyRanges: frequency.getDayFrequencies().entrySet()) {
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
