package com.example.enjoymadrid.servicesimpl;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.User;
import com.example.enjoymadrid.models.repositories.TouristicPointRepository;
import com.example.enjoymadrid.models.repositories.UserRepository;
import com.example.enjoymadrid.services.DictionaryService;
import com.example.enjoymadrid.services.TouristicPointService;
import com.example.enjoymadrid.services.UserService;

@Service
public class TouristicPointServiceImpl implements TouristicPointService {

	private final TouristicPointRepository touristicPointRepository;
	private final UserRepository userRepository;
	private final UserService userService;
	private final DictionaryService dictionaryService;
	
	public TouristicPointServiceImpl(TouristicPointRepository touristicPointRepository, UserRepository userRepository, 
			UserService userService, DictionaryService dictionaryService) {
		this.touristicPointRepository = touristicPointRepository;
		this.userRepository = userRepository;
		this.userService = userService;
		this.dictionaryService = dictionaryService;
	}
	
	@Override
	public List<TouristicPoint> getTouristicPointsByCategory(String category) {
		return this.touristicPointRepository.findByCategory(category);
	}
	
	@Override
	public List<TouristicPoint> getTouristicPointsByQuery(String query) {
		return this.dictionaryService.getTouristicPoints(query);
	}

	@Override
	public List<TouristicPoint> getUserTouristicPoints(Long userId) {
		return this.userService.getUser(userId).getTouristicPoints();
	}

	@Override
	public void addTouristicPointToUser(Long userId, Long touristPointId) {
		// Get user & tourist point from DB
		User user = this.userService.getUser(userId);
		TouristicPoint touristicPoint = this.geTouristicPoint(touristPointId);
		
		// Add tourist point to user & save in DB
		user.getTouristicPoints().add(touristicPoint);
		this.userRepository.save(user);
	}

	@Override
	public void deleteUserTouristicPoint(Long userId, Long touristPointId) {
		// Get user & tourist point from DB
		User user = this.userService.getUser(userId);
		TouristicPoint touristicPoint = this.geTouristicPoint(touristPointId);
		
		// Remove tourist point from user & save in DB
		user.getTouristicPoints().remove(touristicPoint);
		this.userRepository.save(user);
	}
	
	@Override
	public void deleteTouristicPointFromUsers(TouristicPoint touristicPoint) {
		Set<User> users = this.userRepository.findByTouristicPoints(touristicPoint);
		for (User user : users) {
			user.getTouristicPoints().remove(touristicPoint);
			this.userRepository.save(user);
		}
	}
	
	private TouristicPoint geTouristicPoint(Long touristPointId) {
		return this.touristicPointRepository.findById(touristPointId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Punto de interés no encontrado"));
	}

}
