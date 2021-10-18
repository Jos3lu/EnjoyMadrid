package com.enjoymadrid.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
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
		User user1 = new User("Ramon","ramoneitor", new BCryptPasswordEncoder().encode("1fsdfsdAff3"));
		userRepository.save(user1);
		
		User user2 = new User("Pepe", "pepeitor", new BCryptPasswordEncoder().encode("dfdsjhf3213DS"));
		userRepository.save(user2);
		
		User user3 = new User("Juan", "juaneitor", new BCryptPasswordEncoder().encode("dsd321AJDJdfd"));
		userRepository.save(user3);
	}
	
}
