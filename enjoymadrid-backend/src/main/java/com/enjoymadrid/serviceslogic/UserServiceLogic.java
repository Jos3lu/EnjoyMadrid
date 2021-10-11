package com.enjoymadrid.serviceslogic;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.enjoymadrid.model.User;
import com.enjoymadrid.model.repositories.UserRepository;
import com.enjoymadrid.services.UserService;

@Service
@Transactional
public class UserServiceLogic implements UserService {
	
	private UserRepository userRepository;
	
	@Autowired
	public UserServiceLogic(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public User getUser(Long userId) {
		return this.userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));
	}
	
	@Override
	public User createUser(User user) {
		if (!userComplete(user) || (user.getPassword() == null || user.getPassword().isBlank())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Creation not done, bad request of user");
		} else if (this.userRepository.findByEmail(user.getEmail()) != null) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Creation of user not possible");
		} 
		
		user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
		return this.userRepository.save(user);	
	}
	
	@Override
	public User updateUser(Long userId, User updatedUser, MultipartFile imageUser) {
		User pastUser = getUser(userId);
		if (!userComplete(updatedUser)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request of user: " + userId);
		} else if (userNotPossibleModification(pastUser, updatedUser)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Request of user not possible: " + userId);
		} else if (!imageUser.isEmpty()) {
			try {
				updatedUser.setPhoto(imageUser.getBytes());
			} catch (IOException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not update photo of user: " + userId, e);
			}
		}
		
		updatedUser.setId(pastUser.getId());
		updatedUser.setRoutes(pastUser.getRoutes());
		
		if (!(updatedUser.getPassword() == null) && !updatedUser.getPassword().isBlank()) {
			updatedUser.setPassword(new BCryptPasswordEncoder().encode(updatedUser.getPassword()));
		}		
		return this.userRepository.save(updatedUser);
	}
		
	@Override
	public void deleteUser(Long userId) {
		User user = getUser(userId);
		this.userRepository.delete(user);
	}
	
	public boolean userComplete(User user) {
		return user != null && user.getEmail() != null && !user.getEmail().isBlank()
				&& user.getName() != null && !user.getName().isBlank();
	}
	
	public boolean userNotPossibleModification(User pastUser, User updatedUser) {
		// User changes email & already exists in the database
		return !updatedUser.getEmail().equals(pastUser.getEmail()) && 
				this.userRepository.findByEmail(updatedUser.getEmail()) != null;
	}
	
}
