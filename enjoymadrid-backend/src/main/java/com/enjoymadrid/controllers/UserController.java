package com.enjoymadrid.controllers;

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
		
	@PostMapping("/users")
	@JsonView(UserInterfaces.ExtendData.class)
	public ResponseEntity<User> createUser(@RequestBody User user) {
		return new ResponseEntity<User>(this.userService.createUser(user), HttpStatus.CREATED);
	}

	@PutMapping("/users/{id}")
	@JsonView(UserInterfaces.ExtendData.class)
	public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User updatedUser, @RequestParam MultipartFile imageUser) {
		return ResponseEntity.ok(this.userService.updateUser(userId, updatedUser, imageUser));
	}
		
	@DeleteMapping("/users/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
		userService.deleteUser(userId);
		return ResponseEntity.ok().build();
	}
}
