package com.enjoymadrid.serviceslogic;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.enjoymadrid.models.repositories.TouristicPointRepository;
import com.enjoymadrid.models.repositories.TransportPointRepository;
import com.enjoymadrid.models.repositories.UserRepository;
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
	
	public RouteServiceLogic(UserRepository userRepository, TransportPointRepository transportPointRepository,
			TouristicPointRepository touristicPointRepository) {
		this.userRepository = userRepository;
		this.transportPointRepository = transportPointRepository;
		this.touristicPointRepository = touristicPointRepository;
	}

	@Override
	public List<Route> getUserRoutes(Long userId) {
		User user = this.userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
		return user.getRoutes();
	}

	@Override
	public Route createRoute(Route route) {
		
		String coordsOrigin = route.getOrigin().split(": ")[1];
		String coordsDestination = route.getDestination().split(": ")[1];
		
		List<TouristicPoint> touristicPoints = touristicPointRepository.findAll();
		List<TransportPoint> transportPoints = transportPointRepository.findAll();
		
		double d = haversine(51.5007, 0.1246, 40.6892, 74.0445);
		
		// Use filter of list to discard points
		
		return new Route();
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
		double c = 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h)); //2 * Math.asin(Math.sqrt(h));
		
		return R * c * 1000;
	}

}
