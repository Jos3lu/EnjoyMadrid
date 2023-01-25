package com.example.enjoymadrid.serviceslogic;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.enjoymadrid.models.repositories.TouristicPointRepository;
import com.example.enjoymadrid.models.repositories.UserRepository;
import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.User;
import com.example.enjoymadrid.services.DictionaryService;
import com.example.enjoymadrid.services.TouristicPointService;

@Service
public class TouristicPointServiceLogic implements TouristicPointService {

	private final TouristicPointRepository touristicPointRepository;
	private final UserRepository userRepository;
	private final DictionaryService dictionaryService;
	
	public TouristicPointServiceLogic(TouristicPointRepository touristicPointRepository, UserRepository userRepository, 
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
				
		touristicPoint.getUsers().add(user);
		user.getTouristicPoints().add(touristicPoint);
		
		this.touristicPointRepository.save(touristicPoint);
		this.userRepository.save(user);
	}

	@Override
	public void deleteUserTouristicPoint(Long userId, Long touristPointId) {
		User user = this.userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
		TouristicPoint touristicPoint = this.touristicPointRepository.findById(touristPointId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Punto de interés no encontrado"));
		
		touristicPoint.getUsers().remove(user);
		user.getTouristicPoints().remove(touristicPoint);
		
		this.touristicPointRepository.save(touristicPoint);
		this.userRepository.save(user);
	}

}
