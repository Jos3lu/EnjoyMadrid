package com.enjoymadrid.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
	
	private UserService userService;
	
	@GetMapping("/")
	@JsonView(UserInterfaces.BasicData.class)
	public ResponseEntity<List<User>> getUsers() {
		List<User> users = userService.getUsers();
		return !users.isEmpty() ? new ResponseEntity<List<User>>(users, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping("/{id}")
	@JsonView(UserInterfaces.RoutesData.class)
	public ResponseEntity<User> getUser(@PathVariable long id) {
		Optional<User> userOptional = userService.getUser(id);
		if (userOptional.isPresent()) {
			return ResponseEntity.ok(userOptional.get());
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping("/")
	@JsonView(UserInterfaces.BasicData.class)
	public ResponseEntity<User> postUser(@RequestBody User user) {
		User newUser = userService.createUser(user);
		if (newUser == null) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			return new ResponseEntity<>(newUser, HttpStatus.CREATED);
		}
	}

}
