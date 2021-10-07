package com.enjoymadrid.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.enjoymadrid.model.Comment;

@Service
public interface CommentService {
	
	public List<Comment> getUserComments(Long userId);
	
	public List<Comment> getPointComments(Long pointId);
	
	public Comment createComment(Comment comment, Long userId, Long pointId);
	
	public Comment updateComment(Long commentId, Comment updatedcomment);
	
	public void deleteComment(Long commentId);
	
}
