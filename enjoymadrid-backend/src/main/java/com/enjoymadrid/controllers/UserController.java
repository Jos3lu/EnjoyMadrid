package com.enjoymadrid.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enjoymadrid.model.User;
import com.enjoymadrid.model.interfaces.UserInterfaces;
import com.enjoymadrid.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private UserService userService;
		
	@GetMapping("/{id}")
	@JsonView(UserInterfaces.RoutesData.class)
	public ResponseEntity<User> getUser(@PathVariable long id) {
		Optional<User> userOptional = userService.getUser(id);
		if (userOptional.isPresent()) {
			return ResponseEntity.ok(userOptional.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PostMapping("/")
	@JsonView(UserInterfaces.BasicData.class)
	public ResponseEntity<User> createUser(@RequestBody User user) {
		if (!userService.userComplete(user) && user.getPassword() == null) {
			return ResponseEntity.badRequest().build();
		}
		User newUser = userService.createUser(user);
		if (newUser == null) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			return new ResponseEntity<>(newUser, HttpStatus.CREATED);
		}
	}
	
	// Foto por hacer
	
	@PutMapping("/{id}")
	@JsonView(UserInterfaces.BasicData.class)
	public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
		Optional<User> pastUser = userService.getUser(id);
		if (pastUser.isEmpty()) {
			return ResponseEntity.notFound().build();
		} else if (!userService.userComplete(updatedUser)) {
			return ResponseEntity.badRequest().build();
		} else if (userService.userNotPossibleModification(pastUser.get(), updatedUser)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		updatedUser = userService.updateUser(pastUser.get(), updatedUser);
		return ResponseEntity.ok(updatedUser);
	}
	
	@DeleteMapping("/{id}")
	@JsonView(UserInterfaces.BasicData.class)
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		return userService.deleteUser(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}

}
