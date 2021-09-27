package com.enjoymadrid.serviceslogic;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.enjoymadrid.model.User;
import com.enjoymadrid.model.repositories.UserRepository;
import com.enjoymadrid.services.UserService;

@Service
public class UserServiceLogic implements UserService {
	
	@Autowired
	UserRepository userRepository;
	
	@Override
	public User getUser(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}
	
	@Override
	public Resource getImageUser(Long id) {
		User user = getUser(id);
		if (user.getPhoto() == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return new ByteArrayResource(user.getPhoto());
	}

	@Override
	public User createUser(User user) {
		if (!userComplete(user) && user.getPassword() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		} else if (userRepository.findByEmail(user.getEmail()) != null) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
		return userRepository.save(user);	
	}

	@Override
	public void createImageUser(Long id, MultipartFile imageFile) {
		User user = getUser(id);
		try {
			user.setPhoto(imageFile.getBytes());
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		userRepository.save(user);
	}
	
	@Override
	public User updateUser(Long id, User updatedUser) {
		User pastUser = getUser(id);
		if (!userComplete(updatedUser)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		} else if (userNotPossibleModification(pastUser, updatedUser)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		
		updatedUser.setId(pastUser.getId());
		updatedUser.setRoutes(pastUser.getRoutes());
		
		if (!(updatedUser.getPassword() == null) && !updatedUser.getPassword().isBlank()) {
			updatedUser.setPassword(new BCryptPasswordEncoder().encode(updatedUser.getPassword()));
		}		
		return userRepository.save(updatedUser);
	}
	
	@Override
	public void updateImageUser(Long id, MultipartFile imageFile) {
		User user = getUser(id);
		if (user.getPhoto() == null) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		try {
			user.setPhoto(imageFile.getBytes());
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		userRepository.save(user);
	}
	
	@Override
	public void deleteUser(Long id) {
		User user = getUser(id);
		userRepository.delete(user);
	}

	@Override
	public void deleteImageUser(Long id) {
		User user = getUser(id);
		user.setPhoto(null);
		userRepository.save(user);
	}
	
	@Override
	public boolean userComplete(User user) {
		return user != null && user.getEmail() != null 
				&& user.getName() != null;
	}
	
	@Override
	public boolean userNotPossibleModification(User pastUser, User updatedUser) {
		return !updatedUser.getEmail().equals(pastUser.getEmail()) && 
				userRepository.findByEmail(updatedUser.getEmail()) != null;
	}
	
}
