package com.enjoymadrid.serviceslogic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.enjoymadrid.model.Route;
import com.enjoymadrid.model.User;
import com.enjoymadrid.model.repositories.UserRepository;
import com.enjoymadrid.services.RouteService;

@Service
public class RouteServiceLogic implements RouteService {
	
	private UserRepository userRepository;
	
	@Autowired
	public RouteServiceLogic(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public List<Route> getUserRoutes(Long userId) {
		User user = this.userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));
		return user.getRoutes();
	}

}
