package com.example.enjoymadrid.security.jwt;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthTokenFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthTokenFilter.class);

	private final UserDetailsService userDetailsService;
	private final JwtUtilityToken jwtUtilityToken;

	public JwtAuthTokenFilter(UserDetailsService userDetailsService, JwtUtilityToken jwtUtilityToken) {
		this.userDetailsService = userDetailsService;
		this.jwtUtilityToken = jwtUtilityToken;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			String requestAuthHeader = request.getHeader("Authorization");
			if (StringUtils.hasText(requestAuthHeader) && requestAuthHeader.startsWith("Bearer ")) {
				String token = requestAuthHeader.substring(7);
				if (jwtUtilityToken.validateJwtToken(token)) {
					String username = jwtUtilityToken.getUsernameFromJwtToken(token);

					UserDetails userDetails = userDetailsService.loadUserByUsername(username);
					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			}
		} catch (Exception e) {
			logger.error("User authentication not possible: {}", e);
		}

		filterChain.doFilter(request, response);
	}

}
