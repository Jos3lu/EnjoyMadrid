package com.enjoymadrid.serviceslogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.enjoymadrid.models.repositories.AirQualityPointRepository;
import com.enjoymadrid.models.repositories.TouristicPointRepository;
import com.enjoymadrid.models.repositories.TransportPointRepository;
import com.enjoymadrid.models.repositories.UserRepository;
import com.enjoymadrid.models.AirQualityPoint;
import com.enjoymadrid.models.Point;
import com.enjoymadrid.models.PointWrapper;
import com.enjoymadrid.models.Route;
import com.enjoymadrid.models.TouristicPoint;
import com.enjoymadrid.models.TransportPoint;
import com.enjoymadrid.models.User;
import com.enjoymadrid.services.RouteService;

@Service
public class RouteServiceLogic implements RouteService {
	
	private final UserRepository userRepository;
	private final TransportPointRepository transportPointRepository;
	private final TouristicPointRepository touristicPointRepository;
	private final AirQualityPointRepository airQualityPointRepository;
	
	public RouteServiceLogic(UserRepository userRepository, TransportPointRepository transportPointRepository,
			TouristicPointRepository touristicPointRepository, AirQualityPointRepository airQualityPointRepository) {
		this.userRepository = userRepository;
		this.transportPointRepository = transportPointRepository;
		this.touristicPointRepository = touristicPointRepository;
		this.airQualityPointRepository = airQualityPointRepository;
	}

	@Override
	public List<Route> getUserRoutes(Long userId) {
		User user = this.userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
		return user.getRoutes();
	}

	@Override
	public Route createRoute(Route route) {
		
		// Parameters to create route
		TransportPoint origin = route.getOrigin();
		TransportPoint destination = route.getDestination();		
		Double maxDistance = route.getMaxDistance();
		Map<String, Integer> preferences = route.getPreferences();
		
		// Get all the transport points selected by user
		List<String> transports = route.getTransports();
		List<TransportPoint> transportPoints = transportPointRepository.findByTypeIn(transports);
		
		List<TransportPoint> routePoints = findBestRoute(origin, destination, maxDistance, transportPoints, preferences);
		
		routePoints.forEach(point -> System.out.println(point.toString()));

		return new Route();
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
		List<AirQualityPoint> airQualityPoints = airQualityPointRepository.findByAqiIsNotNull();
		// Get all the touristic points
		List<TouristicPoint> touristicPoints = touristicPointRepository.findAll();

		// Add origin point
		PointWrapper<P> originWrapper = new PointWrapper<>(origin, null, 0.0,
				calculateHeuristic(origin, destination, airQualityPoints, touristicPoints, preferences));
		points.put(origin, originWrapper);
		openList.add(originWrapper);

		while (!openList.isEmpty()) {
			PointWrapper<P> pointWrapper = openList.pollFirst();
			P point = pointWrapper.getPoint();
			bestPointsFound.add(point);

			// Point destination reached, return list of points
			if (calculateDistance(point, destination) <= maxDistance) {
				List<P> route = new ArrayList<>();
				while (pointWrapper != null) {
					route.add(0, pointWrapper.getPoint());
					pointWrapper = pointWrapper.getPrevious();
				}
				route.add(destination);
				return route;
			}

			// Iterate over neighbors
			Set<P> neighbors = transportPoints.parallelStream()
					.filter(neighbor -> (calculateDistance(point, neighbor) <= maxDistance))
					.collect(Collectors.toSet());
			for (P neighbor : neighbors) {
				// Continue with next neighbor if already in best points
				if (bestPointsFound.contains(neighbor)) {
					continue;
				}

				// Calculate cost from start to neighbor via current node
				double cost = calculateDistance(point, neighbor);
				double distanceFromOrigin = pointWrapper.getDistanceFromOrigin() + cost;

				// Neighbor not discovered yet
				PointWrapper<P> neighborWrapper = points.get(neighbor);
				if (neighborWrapper == null) {
					neighborWrapper = new PointWrapper<P>(neighbor, pointWrapper, distanceFromOrigin,
							calculateHeuristic(neighbor, destination, airQualityPoints, touristicPoints, preferences));
					points.put(neighbor, neighborWrapper);
					openList.add(neighborWrapper);
				}
				// Neighbor discovered, but total cost via current node is lower -> Update costs
				// & previous point
				else if (distanceFromOrigin < neighborWrapper.getDistanceFromOrigin()) {
					openList.remove(neighborWrapper);
					neighborWrapper.setDistanceFromOrigin(distanceFromOrigin);
					neighborWrapper.setPrevious(pointWrapper);
					openList.add(neighborWrapper);
				}

			}

		}
				
		return null;
	}
	
	private <P extends Comparable<P>> double calculateHeuristic(P point, P destination,
			List<AirQualityPoint> airQualityPoints, List<TouristicPoint> touristicPoints,
			Map<String, Integer> preferences) {
		
		// Calculate distance to destination using haversine formula
		double minDistanceToDestination = calculateDistance(point, destination);

		// Get air quality level from nearest station
		int aqiStation = airQualityPoints.stream()
				.reduce((s1,
						s2) -> haversine(s1.getLatitude(), s1.getLongitude(), ((Point) point).getLatitude(),
								((Point) point).getLongitude()) < haversine(s2.getLatitude(), s2.getLongitude(),
										((Point) point).getLatitude(), ((Point) point).getLongitude()) ? s1 : s2)
				.get().getAqi();

		// Get touristic points within a radius of 500 meters
		List<TouristicPoint> nearTouristicPoints = touristicPoints.parallelStream()
				.filter(touristicPoint -> haversine(touristicPoint.getLatitude(), touristicPoint.getLongitude(),
						((Point) point).getLatitude(), ((Point) point).getLongitude()) <= 0.5)
				.collect(Collectors.toList());

		// Calculate value respect to the number of sites of a given type
		double interestPlaces = preferences.entrySet().stream().reduce(0.0, (sum, preference) -> {
			// Get preference type
			String preferenceName = preference.getKey().substring(preference.getKey().indexOf('_') + 1);
			// Search the touristic point by category attribute
			if (preference.getKey().contains("C_")) {
				sum += nearTouristicPoints.stream()
						.filter(place -> place.getCategories().contains(preferenceName))
						.count() * (preference.getValue() * 2);
			}
			// Preference is a combination of 2 types
			else if (preference.getKey().contains("R_")) {
				sum += nearTouristicPoints.stream()
						.filter(place -> place.getType().equals("Restaurantes") || place.getType().equals("Clubs"))
						.count() * (preference.getValue() * 2);
			}
			// Search the touristic point by type attribute
			else if (preference.getKey().contains("T_")) {
				sum += nearTouristicPoints.stream().filter(place -> place.getType().equals(preferenceName))
						.count() * (preference.getValue() * 2);
			}
			return sum;
		}, (p1, p2) -> p1 + p2);
		
		if (interestPlaces == 0.0) interestPlaces = 1.0;
		
		return (minDistanceToDestination + aqiStation) / interestPlaces;
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

}
