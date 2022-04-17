package com.enjoymadrid.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.enjoymadrid.security.jwt.JwtAuthEntryPoint;
import com.enjoymadrid.security.jwt.JwtAuthTokenFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
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
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http
		// If our API uses token-based authentication, like JWT, we don't need CSRF protection
		.csrf().disable()
		.exceptionHandling().authenticationEntryPoint(jwtAuthEntryPoint).and()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.authorizeRequests()
		.antMatchers(HttpMethod.GET, "/api/users", "/api/users/*", "/api/users/*/routes").authenticated()
		.antMatchers(HttpMethod.POST, "/api/users/*/routes").authenticated()
		.antMatchers(HttpMethod.PUT, "/api/users/*", "/api/users/*/picture").authenticated()
		.antMatchers(HttpMethod.DELETE, "/api/users/*", "/api/users/*/routes/*").authenticated()
		.antMatchers("/**").permitAll();
					
		// jwtAuthTokenFilter triggers before UsernamePasswordAuthenticationFilter
		http.addFilterBefore(jwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class);
	}
		
}
