package com.enjoymadrid.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;

import com.enjoymadrid.model.User;
import com.enjoymadrid.model.repositories.UserRepository;

@Controller
public class DataBase implements CommandLineRunner{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public void run(String... args) throws Exception {
		userRepository.save(new User("Ramon","ramon@gmail.com", new BCryptPasswordEncoder().encode("1fsdfsdAff3")));
		userRepository.save(new User("Pepe", "pepe@gmail.com", new BCryptPasswordEncoder().encode("dfdsjhf3213DS")));
		userRepository.save(new User("Juan", "juan@gmail.com", new BCryptPasswordEncoder().encode("dsd321AJDJdfd")));
	}
	
}
