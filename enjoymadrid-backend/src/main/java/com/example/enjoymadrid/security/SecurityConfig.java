package com.example.enjoymadrid.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.enjoymadrid.security.jwt.JwtAuthEntryPoint;
import com.example.enjoymadrid.security.jwt.JwtAuthTokenFilter;

@Configuration
public class SecurityConfig {
	
	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	JwtAuthEntryPoint jwtAuthEntryPoint;
	
	@Autowired
	JwtAuthTokenFilter jwtAuthTokenFilter;
		
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
	
	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http
		// If our API uses token-based authentication, like JWT, we don't need CSRF protection
		.csrf().disable()
		.exceptionHandling().authenticationEntryPoint(jwtAuthEntryPoint).and()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.authorizeHttpRequests()
		.requestMatchers(HttpMethod.GET, "/api/users", "/api/users/*", "/api/users/*/routes").authenticated()
		.requestMatchers(HttpMethod.POST, "/api/users/*/routes").authenticated()
		.requestMatchers(HttpMethod.PUT, "/api/users/*", "/api/users/*/picture").authenticated()
		.requestMatchers(HttpMethod.DELETE, "/api/users/*", "/api/users/*/routes/*").authenticated()
		.requestMatchers("/**").permitAll();
		
		http.headers().frameOptions().disable();
				
		// jwtAuthTokenFilter triggers before UsernamePasswordAuthenticationFilter
		http.addFilterBefore(jwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
		
}
