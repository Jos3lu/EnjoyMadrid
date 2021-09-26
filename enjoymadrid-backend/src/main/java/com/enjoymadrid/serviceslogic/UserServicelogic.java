package com.enjoymadrid.serviceslogic;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.enjoymadrid.components.UserComponent;
import com.enjoymadrid.model.User;
import com.enjoymadrid.model.repositories.UserRepository;
import com.enjoymadrid.services.UserService;

@Service
public class UserServicelogic implements UserService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserComponent userComponent;

	@Override
	public Optional<User> getUser(Long id) {
		return userRepository.findById(id);
	}

	@Override
	public User createUser(User user) {
		if (userRepository.findByEmail(user.getEmail()) != null) {
			return null;
		} else {
			user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
			userRepository.save(user);
			return user;
		}
	}

	@Override
	public User updateUser(User pastUser, User updatedUser) {
		updatedUser.setId(pastUser.getId());
		updatedUser.setRoutes(pastUser.getRoutes());
		
		if (!(updatedUser.getPassword() == null) && !updatedUser.getPassword().isBlank()) {
			updatedUser.setPassword(new BCryptPasswordEncoder().encode(updatedUser.getPassword()));
		}
		
		return userRepository.save(updatedUser);
	}
	
	@Override
	public boolean deleteUser(Long id) {
		Optional<User> user = getUser(id);
		if (user.isPresent()) {
			userRepository.delete(user.get());
			return true;
		}
		return false;
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
