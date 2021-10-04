package com.enjoymadrid.services;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.enjoymadrid.model.User;

@Service
public interface UserService {
	
	public User getUser(Long id);
	
	public Resource getImageUser(Long id);
	
	public User createUser(User user);
	
	public void createImageUser(Long id, MultipartFile imageFile);
	
	public User updateUser(Long id, User updatedUser);
	
	public void updateImageUser(Long id, MultipartFile imageFile);
	
	public void deleteUser(Long id);
	
	public void deleteImageUser(Long id);
		
}
