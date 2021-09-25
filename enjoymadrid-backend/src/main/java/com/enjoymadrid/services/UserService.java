package com.enjoymadrid.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.enjoymadrid.model.User;

@Service
public interface UserService {

	public List<User> getUsers();
	
	public Optional<User> getUser(Long userId);
	
	public User createUser(User user);
	
}
