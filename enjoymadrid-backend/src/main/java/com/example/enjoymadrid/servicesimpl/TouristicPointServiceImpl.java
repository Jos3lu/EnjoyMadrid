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

@Service
public class TouristicPointServiceImpl implements TouristicPointService {

	private final TouristicPointRepository touristicPointRepository;
	private final UserRepository userRepository;
	private final DictionaryService dictionaryService;
	
	public TouristicPointServiceImpl(TouristicPointRepository touristicPointRepository, UserRepository userRepository, 
			DictionaryService dictionaryService) {
		this.touristicPointRepository = touristicPointRepository;
		this.userRepository = userRepository;
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
		return this.userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"))
				.getTouristicPoints();
	}

	@Override
	public void addTouristicPointToUser(Long userId, Long touristPointId) {
		User user = this.userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
		TouristicPoint touristicPoint = this.touristicPointRepository.findById(touristPointId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Punto de interés no encontrado"));
		
		user.getTouristicPoints().add(touristicPoint);
		
		this.userRepository.save(user);
	}

	@Override
	public void deleteUserTouristicPoint(Long userId, Long touristPointId) {
		User user = this.userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
		TouristicPoint touristicPoint = this.touristicPointRepository.findById(touristPointId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Punto de interés no encontrado"));
		
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

}
