package com.example.enjoymadrid.models;

import java.util.Objects;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.example.enjoymadrid.models.interfaces.PointInterfaces;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "POINT_TYPE")
public class Point {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(PointInterfaces.BasicData.class)
	private Long id;
	
	@JsonView(PointInterfaces.BasicData.class)
	@NotEmpty(message = "Nombre no puede estar vacío")
	private String name;
	
	@JsonView(PointInterfaces.BasicData.class)
	@NotNull(message = "Longitud no puede estar vacío")
	private Double longitude;
	
	@JsonView(PointInterfaces.BasicData.class)
	@NotNull(message = "Latitud no puede estar vacío")
	private Double latitude;
	
	public Point() {}

	public Point(@NotEmpty(message = "Nombre no puede estar vacío") String name,
			@NotNull(message = "Longitud no puede estar vacío") Double longitude,
			@NotNull(message = "Latitud no puede estar vacío") Double latitude) {
		this.name = name;
		this.longitude = longitude;
		this.latitude = latitude;
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

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	@Override
	public int hashCode() {
		return Objects.hash(latitude, longitude, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		return Objects.equals(latitude, other.latitude) && Objects.equals(longitude, other.longitude)
				&& Objects.equals(name, other.name);
	}
	
}
