package com.enjoymadrid.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;

import com.enjoymadrid.model.User;
import com.enjoymadrid.model.repositories.UserRepository;

@Controller
public class DataBase implements CommandLineRunner{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public void run(String... args) throws Exception {
		userRepository.save(new User("Ramon","ramon@gmail.com", "12345"));
		userRepository.save(new User("Pepe", "pepe@gmail.com", "abcde"));
		userRepository.save(new User("Juan", "juan@gmail.com", "sjdhf"));
	}
	
}
