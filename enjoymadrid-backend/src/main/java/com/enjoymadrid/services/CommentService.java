package com.enjoymadrid.services;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.enjoymadrid.model.Comment;

@Service
public interface CommentService {
	
	public List<Comment> getUserComments(Long userId);
	
	public List<Comment> getPointComments(Long pointId);
	
	public Comment createComment(@Valid Comment comment, Long userId, Long pointId);
	
	public Comment updateComment(Long commentId, @Valid Comment updatedcomment);
	
	public void deleteComment(Long commentId);
	
}
