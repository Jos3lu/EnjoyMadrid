package com.enjoymadrid.models;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.enjoymadrid.models.interfaces.RouteInterfaces;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class Route {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(RouteInterfaces.BasicData.class)
	private Long id;
	
	@JsonView(RouteInterfaces.BasicData.class)
	@NotBlank(message = "El nombre no puede estar vacío")
	private String name;
	
	@JsonView(RouteInterfaces.BasicData.class)
	@NotNull(message = "La fecha no puede estar vacía")
	private LocalDate date;
	
	@JsonView(RouteInterfaces.BasicData.class)
	private Integer numPoints;
				
	//@ManyToMany
	//@JoinTable(name="route_point", joinColumns=@JoinColumn(name="route_id"), inverseJoinColumns=@JoinColumn(name="point_id"))
	//@JsonView(RouteInterfaces.PointsData.class)
	//private List<Point> points = new LinkedList<>();
		
	public Route() {}
	
}
