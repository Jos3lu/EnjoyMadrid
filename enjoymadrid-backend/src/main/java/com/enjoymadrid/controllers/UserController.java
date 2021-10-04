package com.enjoymadrid.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
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
@RequestMapping("/api/users")
public class UserController {
	
	private UserService userService;
	
	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}
		
	@GetMapping("/{id}")
	@JsonView(UserInterfaces.RoutesData.class)
	public ResponseEntity<User> getUser(@PathVariable Long id) {
		return ResponseEntity.ok(userService.getUser(id));
	}
	
	@GetMapping("/{id}/image")
	public ResponseEntity<Resource> getImageUser(@PathVariable Long id) {
		return ResponseEntity.ok(userService.getImageUser(id));
	}
	
	@PostMapping("/")
	@JsonView(UserInterfaces.BasicData.class)
	public ResponseEntity<User> createUser(@RequestBody User user) {
		return new ResponseEntity<User>(userService.createUser(user), HttpStatus.CREATED);
	}
	
	@PostMapping("/{id}/image")
	public ResponseEntity<Void> createImageUser(@PathVariable Long id, @RequestParam MultipartFile imageFile) {
		userService.createImageUser(id, imageFile);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}")
	@JsonView(UserInterfaces.BasicData.class)
	public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
		return ResponseEntity.ok(userService.updateUser(id, updatedUser));
	}
	
	@PutMapping("/{id}/image")
	public ResponseEntity<Void> updateImageUser(@PathVariable Long id, @RequestParam MultipartFile imageFile) {
		userService.updateImageUser(id, imageFile);
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/{id}/image")
	@JsonView(UserInterfaces.BasicData.class)
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteImageUser(@PathVariable Long id) {
		userService.deleteImageUser(id);
		return ResponseEntity.ok().build();
	}
	
}
