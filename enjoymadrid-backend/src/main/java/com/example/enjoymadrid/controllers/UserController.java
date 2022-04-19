package com.example.enjoymadrid.controllers;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.enjoymadrid.models.dtos.UserCreateDto;
import com.example.enjoymadrid.models.dtos.UserUpdateDto;
import com.example.enjoymadrid.models.interfaces.UserInterfaces;
import com.example.enjoymadrid.models.User;
import com.example.enjoymadrid.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/api")
public class UserController {
	
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping("/signup")
	public ResponseEntity<Void> createUser(@Valid @RequestBody UserCreateDto userDto) {
		User user = new User(userDto.getName(), userDto.getUsername(), userDto.getPassword());
		this.userService.createUser(user);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
		
	@PutMapping("/users/{userId}")
	@JsonView(UserInterfaces.UserData.class)
	public ResponseEntity<User> updateUser(@PathVariable Long userId, @Valid @RequestBody UserUpdateDto updatedUserDto) {
		User updatedUser = new User(updatedUserDto.getName(), updatedUserDto.getUsername(), updatedUserDto.getPassword());
		return ResponseEntity.ok(this.userService.updateUser(userId, updatedUser, updatedUserDto.getOldPassword()));
	}
	
	@PutMapping("/users/{userId}/picture")
	@JsonView(UserInterfaces.PictureData.class)
	public ResponseEntity<User> updateUserImage(@PathVariable Long userId, @RequestParam MultipartFile imageUser) {
		return ResponseEntity.ok(this.userService.updateUserImage(userId, imageUser));
	}
		
	@DeleteMapping("/users/{userId}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
		this.userService.deleteUser(userId);
		return ResponseEntity.ok().build();
	}
}
