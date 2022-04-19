package com.example.enjoymadrid.serviceslogic;

import java.util.LinkedList;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.enjoymadrid.models.repositories.UserRepository;
import com.example.enjoymadrid.models.User;

@Service
public class UserDetailsServiceLogic implements UserDetailsService {

	private final UserRepository userRepository;
	
	public UserDetailsServiceLogic(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new LinkedList<>());
	}	
		
}
