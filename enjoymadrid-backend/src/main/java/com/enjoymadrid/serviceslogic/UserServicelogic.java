package com.enjoymadrid.serviceslogic;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.enjoymadrid.model.User;
import com.enjoymadrid.model.repositories.UserRepository;
import com.enjoymadrid.services.UserService;

@Service
public class UserServicelogic implements UserService {
	
	@Autowired
	UserRepository userRepository;

	@Override
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@Override
	public Optional<User> getUser(Long userId) {
		return userRepository.findById(userId);
	}

	@Override
	public User createUser(User user) {
		if (userRepository.findByEmail(user.getEmail()) != null) {
			return null;
		} else {
			user.setId(null);
			user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
			userRepository.save(user);
			return user;
		}
	}
	
	
	
}
