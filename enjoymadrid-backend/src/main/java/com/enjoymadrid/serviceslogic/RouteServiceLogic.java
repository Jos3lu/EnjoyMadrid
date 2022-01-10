package com.enjoymadrid.serviceslogic;

import java.util.ArrayList;
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
	
	private <N extends Comparable<N>> List<N> findBestRoute(N origin, N destination, Double maxDistance, List<N> transportPoints, Map<String, Integer> preferences) {
		
		// Map that delivers the wrapper for a point
		Map<N, PointWrapper<N>> points = new HashMap<>();
		// Iterate over the points ordered by best cost
		TreeSet<PointWrapper<N>> openList = new TreeSet<>();
		// Check if a point has already been processed
		Set<N> bestPointsFound = new HashSet<>();
		
		// Add origin point
		PointWrapper<N> originWrapper = new PointWrapper<>(origin, null, 0.0, calculateDistance(origin, destination));
		points.put(origin, originWrapper);
		openList.add(originWrapper);
		
		while (!openList.isEmpty()) {
			PointWrapper<N> pointWrapper = openList.pollFirst();
			N point = pointWrapper.getPoint();
			bestPointsFound.add(point);
			
			// Point destination reached, return list of points
			if (calculateDistance(point, destination) <= maxDistance) {
				List<N> route = new ArrayList<>();
				while (pointWrapper != null) {
					route.add(0, pointWrapper.getPoint());
					pointWrapper = pointWrapper.getPrevious();
				}
				route.add(destination);
				return route;
			}
			
			// Iterate over neighbors
			Set<N> neighbors = transportPoints.parallelStream().filter(neighbor -> (calculateDistance(point, neighbor) <= maxDistance)).collect(Collectors.toSet());
			for (N neighbor: neighbors) {
				// Continue with next neighbor if already in best points
				if (bestPointsFound.contains(neighbor)) {
					continue;
				}
				
				// Calculate cost from start to neighbor via current node
				double cost = calculateDistance(point, neighbor);
				double totalCostFromStart = pointWrapper.getTotalCostFromStart() + cost;
				
				// Neighbor not discovered yet
				PointWrapper<N> neighborWrapper = points.get(neighbor);
				if (neighborWrapper == null) {
					neighborWrapper = new PointWrapper<N>(neighbor, pointWrapper, totalCostFromStart, calculateDistance(neighbor, destination));
					points.put(neighbor, neighborWrapper);
					openList.add(neighborWrapper);
				} 
				// Neighbor discovered, but total cost via current node is lower -> Update costs & previous point
				else if (totalCostFromStart < neighborWrapper.getTotalCostFromStart()) {
					openList.remove(neighborWrapper);
					neighborWrapper.setTotalCostFromStart(totalCostFromStart);
					neighborWrapper.setPrevious(pointWrapper);
					openList.add(neighborWrapper);
				}
				
			}
			
		}
		
		// Use filter of stream to discard points
		
		//List<AirQualityPoint> airQualityPoints = airQualityPointRepository.findAll();
		//List<TouristicPoint> touristicPoints = touristicPointRepository.findAll();
		
		return null;
	}
	
	private <N extends Comparable<N>> double calculateDistance(N origin, N destination) {
		Point source = (Point) origin;
		Point target = (Point) destination;
		return haversine(source.getLatitude(), source.getLongitude(), target.getLatitude(), target.getLongitude());
	}
	
	/**
	 * Calculate distance between two points on Earth
	 * @param lat1/lon1 start point latitude/longitude
	 * @param lat2/lat2 end point latitude/longitude
	 * @return Distance in meters
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
