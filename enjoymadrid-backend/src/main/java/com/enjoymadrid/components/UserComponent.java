package com.enjoymadrid.components;



import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.enjoymadrid.models.Route;

@Component
@SessionScope
public class UserComponent {

	private Map<Long, Route> routes = new HashMap<>();

	public Map<Long, Route> getRoutes() {
		return routes;
	}

	public void setRoutes(Map<Long, Route> routes) {
		this.routes = routes;
	}
	
}
