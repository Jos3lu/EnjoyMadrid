package com.example.enjoymadrid.services;

import org.springframework.web.multipart.MultipartFile;

import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.User;

public interface UserService {
	
	/**
	 * Get the information of a user
	 * 
	 * @param userId ID of a user
	 * @return User
	 */
	public User getUser(Long userId);
	
	/**
	 * Get the information of a user by the username
	 * 
	 * @param username Username of user
	 * @return User
	 */
	public User getUserByUsername(String username);
	
	/**
	 * Create a new user
	 * 
	 * @param user User information
	 */
	public void createUser(User user);
	
	/**
	 * Update the information of a user
	 * 
	 * @param userId ID of a user
	 * @param updatedUser Updated user
	 * @param oldPassword Previous user password
	 * @return Updated user information
	 */
	public User updateUser(Long userId, User updatedUser, String oldPassword);
	
	/**
	 * Update the picture of a user
	 * 
	 * @param userId ID of a user
	 * @param imageUser Picture to update
	 * @return User
	 */
	public User updateUserImage(Long userId, MultipartFile imageUser);
	
	/**
	 * Delete a user from DB
	 * 
	 * @param userId ID of a user
	 */
	public void deleteUser(Long userId);
	
	/**
	 * Unbundled a user from a tourist point
	 * 
	 * @param user User
	 * @param point Tourist point
	 */
	public void deleteTouristicPointOfUser(User user, TouristicPoint point);
		
}
