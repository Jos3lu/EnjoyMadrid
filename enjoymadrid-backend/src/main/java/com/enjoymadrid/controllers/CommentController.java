package com.enjoymadrid.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.enjoymadrid.model.Comment;
import com.enjoymadrid.model.interfaces.CommentInterfaces;
import com.enjoymadrid.model.interfaces.PointInterfaces;
import com.enjoymadrid.model.interfaces.UserInterfaces;
import com.enjoymadrid.services.CommentService;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/api")
public class CommentController {

	private final CommentService commentService;

	public CommentController(CommentService commentService) {
		this.commentService = commentService;
	}
		
	@GetMapping("/users/{userId}/comments")
	@JsonView(UserInterfaces.CommentData.class)
	public ResponseEntity<List<Comment>> getUserComments(@PathVariable Long userId) {
		List<Comment> comments = this.commentService.getUserComments(userId);
		return comments.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(comments);
	}
	
	@GetMapping("/points/{pointId}/comments")
	@JsonView(PointInterfaces.CommentData.class)
	public ResponseEntity<List<Comment>> getPointComments(@PathVariable Long pointId) {
		List<Comment> comments = this.commentService.getPointComments(pointId);
		return comments.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(comments);
	}
	
	@PostMapping("/comments")
	@JsonView(CommentInterfaces.BasicData.class)
	public ResponseEntity<Comment> createComment(@RequestBody Comment comment, @RequestParam Long userId, @RequestParam Long pointId) {
		return new ResponseEntity<Comment>(this.commentService.createComment(comment, userId, pointId), HttpStatus.CREATED);
	}
	
	@PutMapping("/comments/{commentId}")
	@JsonView(CommentInterfaces.BasicData.class)
	public ResponseEntity<Comment> updateComment(@PathVariable Long commentId, @RequestBody Comment updatedcomment) {
		return ResponseEntity.ok(this.commentService.updateComment(commentId, updatedcomment));
	}
	
	@DeleteMapping("/comments/{commentId}")
	public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
		this.commentService.deleteComment(commentId);
		return ResponseEntity.ok().build();
	}
	
}
