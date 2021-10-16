package com.enjoymadrid.serviceslogic;

import java.util.LinkedList;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enjoymadrid.model.User;
import com.enjoymadrid.model.repositories.UserRepository;

@Service
public class UserDetailsServiceLogic implements UserDetailsService {

	private final UserRepository userRepository;
	
	public UserDetailsServiceLogic(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new LinkedList<>());
	}	
		
}
