package com.example.enjoymadrid.controllertests.utils;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class MockAuthentication implements Authentication {

	private static final long serialVersionUID = 7405949462979709124L;
	
	public MockAuthentication() {
		
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getDetails() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return null;
	}

	@Override
	public boolean isAuthenticated() {
		return false;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		
	}

}
