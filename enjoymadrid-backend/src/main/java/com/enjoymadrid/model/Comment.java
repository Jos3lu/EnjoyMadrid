package com.enjoymadrid.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

import com.enjoymadrid.model.interfaces.CommentInterfaces;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(CommentInterfaces.BasicData.class)
	private Long id;
	
	@Lob
	@JsonView(CommentInterfaces.BasicData.class)
	@NotBlank(message = "El comentario no puede estar vacío")
	private String commentary;
	
	@JsonView(CommentInterfaces.BasicData.class)
	private LocalDate date;
	
	@ManyToOne
	@JsonView(CommentInterfaces.UserData.class)
	private User user;
	
	@ManyToOne
	@JsonView(CommentInterfaces.PointData.class)
	private Point point;
	
	public Comment() {}

	public Comment(String commentary, LocalDate date) {
		this.commentary = commentary;
		this.date = date;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCommentary() {
		return commentary;
	}

	public void setCommentary(String commentary) {
		this.commentary = commentary;
	}
	
	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}
	
}
