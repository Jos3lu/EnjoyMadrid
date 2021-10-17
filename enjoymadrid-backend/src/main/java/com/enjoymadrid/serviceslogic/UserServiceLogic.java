package com.enjoymadrid.serviceslogic;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.enjoymadrid.model.User;
import com.enjoymadrid.model.repositories.UserRepository;
import com.enjoymadrid.services.UserService;

@Service
public class UserServiceLogic implements UserService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public UserServiceLogic(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User getUser(Long userId) {
		return this.userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));
	}
	
	@Override
	public User getUserByUsername(String username) {
		return this.userRepository.findByUsername(username).orElse(null);
	}
	
	@Override
	public User createUser(User user) {
		if (this.userRepository.existsByUsername(user.getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Creation of user not possible, username already exists");
		} else if (user.getPassword() == null || user.getPassword().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be empty, bad request");
		}
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return this.userRepository.save(user);	
	}
	
	@Override
	public User updateUser(Long userId, User updatedUser) {
		User pastUser = getUser(userId);
		if (userNotPossibleModification(pastUser, updatedUser)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Request of user not possible: " + userId);
		}
		
		updatedUser.setId(pastUser.getId());
		updatedUser.setRoutes(pastUser.getRoutes());
		updatedUser.setComments(pastUser.getComments());
		if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
			updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
		}	
		
		return this.userRepository.save(updatedUser);
	}
	
	@Override
	public User updateUserImage(Long userId, MultipartFile imageUser) {
		User user = getUser(userId);
		try {
			user.setPhoto(imageUser.getBytes());
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not update photo of user: " + userId, e);
		}
		return this.userRepository.save(user);
	}
		
	@Override
	public void deleteUser(Long userId) {
		User user = getUser(userId);
		this.userRepository.delete(user);
	}
		
	private boolean userNotPossibleModification(User pastUser, User updatedUser) {
		// User changes email & already exists in the database
		return !updatedUser.getUsername().equals(pastUser.getUsername()) && 
				this.userRepository.existsByUsername(updatedUser.getUsername());
	}
	
}
