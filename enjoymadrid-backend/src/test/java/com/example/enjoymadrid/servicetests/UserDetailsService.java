package com.example.enjoymadrid.servicetests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestPropertySource;

import com.example.enjoymadrid.models.User;
import com.example.enjoymadrid.models.repositories.UserRepository;
import com.example.enjoymadrid.servicesimpl.UserDetailsServiceImpl;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
public class UserDetailsService {

	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private UserDetailsServiceImpl userDetailsService;
	
	@Test
	public void loadUserByUsername() {
		User expectedUser = new User("Sam", "SamSmith", "12345ABCdef");
		
		when(userRepository.findByUsername(anyString()))
			.thenReturn(Optional.of(expectedUser));
		
		UserDetails userDetails = userDetailsService.loadUserByUsername("SamSmith");
		
		assertThat(userDetails.getUsername()).isEqualTo("SamSmith");
		assertThat(userDetails.getPassword()).isEqualTo("12345ABCdef");
		assertThat(userDetails.getAuthorities()).isEmpty();
		verify(userRepository).findByUsername("SamSmith");
	}
	
	@Test
	public void loadUserByUsername_exception() {
		// User not found
		when(userRepository.findByUsername(anyString()))
			.thenReturn(Optional.empty());
		
		assertThrows(UsernameNotFoundException.class, 
				() -> userDetailsService.loadUserByUsername("SamSmith"));
		verify(userRepository).findByUsername("SamSmith");
	}
	
}
