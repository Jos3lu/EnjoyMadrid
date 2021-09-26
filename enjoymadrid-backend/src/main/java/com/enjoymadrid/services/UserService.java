package com.enjoymadrid.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.enjoymadrid.model.User;

@Service
public interface UserService {
	
	public Optional<User> getUser(Long id);
	
	public User createUser(User user);
	
	public User updateUser(User pastUser, User updatedUser);
	
	public boolean deleteUser(Long id);
	
	public boolean userComplete(User user);
	
	public boolean userNotPossibleModification(User pastUser, User updatedUser);
	
}
