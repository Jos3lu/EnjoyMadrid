package com.example.enjoymadrid.services;

import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.User;

@Service
public interface UserService {
	
	public User getUser(Long userId);
	
	public User getUserByUsername(String username);
		
	public void createUser(@Valid User user);
		
	public User updateUser(Long userId, @Valid User updatedUser, String oldPassword);
	
	public User updateUserImage(Long userId, MultipartFile imageUser);
	
	public void deleteUser(Long userId);
	
	public void deleteTouristicPointOfUser(@Valid User user, @Valid TouristicPoint point);
		
}
