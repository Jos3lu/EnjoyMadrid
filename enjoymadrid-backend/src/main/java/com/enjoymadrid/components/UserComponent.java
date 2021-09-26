package com.enjoymadrid.components;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.enjoymadrid.model.User;

@Component
@SessionScope
public class UserComponent {
	
	private User loggedUser;
	
	public User getLoggedUser() {
		return loggedUser;
	}
	
	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}
	
	public boolean isLoggedUser() {
		return this.loggedUser != null;
	}
	
}
