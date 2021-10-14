package com.enjoymadrid.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enjoymadrid.model.User;
import com.enjoymadrid.model.interfaces.UserInterfaces;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/api")
public class LoginController {

	@GetMapping("/login")
	@JsonView(UserInterfaces.BasicData.class)
	public ResponseEntity<User> login(@AuthenticationPrincipal User user) {
		return user == null ? new ResponseEntity<User>(user, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	}
	
	@GetMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response, @AuthenticationPrincipal Authentication auth) {
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	}
	
}
