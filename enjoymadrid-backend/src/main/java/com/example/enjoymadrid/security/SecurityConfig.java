package com.example.enjoymadrid.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.enjoymadrid.security.jwt.JwtAuthEntryPoint;
import com.example.enjoymadrid.security.jwt.JwtAuthTokenFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	JwtAuthEntryPoint jwtAuthEntryPoint;
	
	@Autowired
	JwtAuthTokenFilter jwtAuthTokenFilter;
	
    @Bean
    PasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder();
    }
    
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
    	return authConfiguration.getAuthenticationManager();
    }
	
    @Bean
    DaoAuthenticationProvider authenticationProvider() {
    	DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    	
    	authProvider.setUserDetailsService(userDetailsService);
    	authProvider.setPasswordEncoder(passwordEncoder());
    	
    	return authProvider;
    }
    
	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http
		// If our API uses token-based authentication, like JWT, we don't need CSRF protection
		.cors().and().csrf().disable()
		.exceptionHandling().authenticationEntryPoint(jwtAuthEntryPoint).and()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.authorizeHttpRequests()
		.requestMatchers(HttpMethod.GET, "/api/users", "/api/users/*", "/api/users/*/routes", "/api/users/*/tourist-points").authenticated()
		.requestMatchers(HttpMethod.POST, "/api/users/*/routes", "/api/users/*/tourist-points/*").authenticated()
		.requestMatchers(HttpMethod.PUT, "/api/users/*", "/api/users/*/picture").authenticated()
		.requestMatchers(HttpMethod.DELETE, "/api/users/*", "/api/users/*/routes/*", "/api/users/*/tourist-points/*").authenticated()
		.requestMatchers("/**").permitAll();
		
		http.headers().frameOptions().disable();
		
		// Set our authentication provider
		http.authenticationProvider(authenticationProvider());
				
		// jwtAuthTokenFilter triggers before UsernamePasswordAuthenticationFilter
		http.addFilterBefore(jwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
		
}
