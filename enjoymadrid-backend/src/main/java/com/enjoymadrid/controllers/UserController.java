package com.enjoymadrid.controllers;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.enjoymadrid.model.User;
import com.enjoymadrid.model.dtos.UserDto;
import com.enjoymadrid.model.interfaces.UserInterfaces;
import com.enjoymadrid.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/api")
public class UserController {
	
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}
		
	@GetMapping("/users/{userId}")
	@JsonView(UserInterfaces.ExtendData.class)
	public ResponseEntity<User> getUser(@PathVariable Long userId) {
		return ResponseEntity.ok(this.userService.getUser(userId));
	}
	
	@GetMapping("/users")
	@JsonView(UserInterfaces.UsernameData.class)
	public ResponseEntity<User> getUserByUsername(@RequestParam String username) {
		return ResponseEntity.ok(this.userService.getUserByUsername(username));
	}
	
	@PostMapping("/signup")
	@JsonView(UserInterfaces.UsernameData.class)
	public ResponseEntity<User> createUser(@Valid @RequestBody UserDto userDto) {
		User user = new User(userDto.getName(), userDto.getUsername(), userDto.getPassword());
		return new ResponseEntity<User>(this.userService.createUser(user), HttpStatus.CREATED);
	}
		
	@PutMapping("/users/{userId}")
	@JsonView(UserInterfaces.UsernameData.class)
	public ResponseEntity<User> updateUser(@PathVariable Long userId, @Valid @RequestBody UserDto updatedUserDto) {
		User updatedUser = new User(updatedUserDto.getName(), updatedUserDto.getUsername(), updatedUserDto.getPassword());
		return ResponseEntity.ok(this.userService.updateUser(userId, updatedUser));
	}
	
	@PutMapping("/users/{userId}/picture")
	@JsonView(UserInterfaces.PictureData.class)
	public ResponseEntity<User> updateUserImage(@PathVariable Long userId, @RequestParam MultipartFile imageUser) {
		return ResponseEntity.ok(this.userService.updateUserImage(userId, imageUser));
	}
		
	@DeleteMapping("/users/{userId}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
		userService.deleteUser(userId);
		return ResponseEntity.ok().build();
	}
}
