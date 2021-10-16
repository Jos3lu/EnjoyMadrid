package com.enjoymadrid.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
	@NotBlank(message = "Comment cannot be empty")
	private String commentary;
	
	@ManyToOne
	@JsonView(CommentInterfaces.UserData.class)
	private User user;
	
	@ManyToOne
	@JsonView(CommentInterfaces.PointData.class)
	private Point point;
	
	public Comment() {}

	public Comment(String commentary) {
		this.commentary = commentary;
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
