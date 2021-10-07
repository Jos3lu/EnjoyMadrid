package com.enjoymadrid.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.enjoymadrid.model.User;

@Service
public interface UserService {
	
	public User getUser(Long userId);
		
	public User createUser(User user, MultipartFile imageUser);
		
	public User updateUser(Long userId, User updatedUser,MultipartFile imageUser);
	
	public void deleteUser(Long userId);
		
}
