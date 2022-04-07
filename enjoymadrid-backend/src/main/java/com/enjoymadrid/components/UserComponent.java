package com.enjoymadrid.components;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.enjoymadrid.models.Route;

@Component
@SessionScope
public class UserComponent {

	private List<Route> routes = new ArrayList<>();

	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}
	
}
