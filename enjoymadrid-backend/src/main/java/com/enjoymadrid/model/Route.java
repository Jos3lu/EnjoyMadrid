package com.enjoymadrid.model;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.enjoymadrid.model.interfaces.RouteInterfaces;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class Route {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(RouteInterfaces.BasicData.class)
	private Long id;
	
	@JsonView(RouteInterfaces.BasicData.class)
	private String name;
	
	@OneToMany(mappedBy = "route", orphanRemoval = true, cascade = CascadeType.REMOVE)
	@JsonView(RouteInterfaces.PointsData.class)
	private List<Point> points;
	
	@ManyToOne
	@JsonView(RouteInterfaces.UserData.class)
	private User user;
	
	public Route() {
		points = new LinkedList<>();
	}
	
	public Route(String name) {
		this();
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Route [id=" + id + ", name=" + name + ", points=" + points + ", user=" + user + "]";
	}
	
}
