package com.enjoymadrid.serviceslogic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.enjoymadrid.model.Comment;
import com.enjoymadrid.model.Point;
import com.enjoymadrid.model.User;
import com.enjoymadrid.model.repositories.CommentRepository;
import com.enjoymadrid.model.repositories.PointRepository;
import com.enjoymadrid.model.repositories.UserRepository;
import com.enjoymadrid.services.CommentService;

@Service
public class CommentServiceLogic implements CommentService {

	private CommentRepository commentRepository;
	private UserRepository userRepository;
	private PointRepository pointRepository;
	
	@Autowired
	public CommentServiceLogic(CommentRepository commentRepository, UserRepository userRepository, PointRepository pointRepository) {
		this.commentRepository = commentRepository;
		this.userRepository = userRepository;
		this.pointRepository = pointRepository;
	}
	
	@Override
	public List<Comment> getUserComments(Long userId) {
		User user = this.userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));
		return user.getComments();
	}

	@Override
	public List<Comment> getPointComments(Long pointId) {
		Point point = this.pointRepository.findById(pointId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Point not found: " + pointId));
		return point.getComments();
	}
	
	@Override
	public Comment createComment(Comment comment, Long userId, Long pointId) {
		User user = this.userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User of comment not found: " + userId));
		Point point = this.pointRepository.findById(pointId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Point of comment not found: " + pointId));
		comment.setUser(user);
		comment.setPoint(point);
		return this.commentRepository.save(comment);
	}

	@Override
	public Comment updateComment(Long commentId, Comment updatedComment) {
		Comment comment = this.commentRepository.findById(commentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found: " + commentId));
		comment.setCommentary(updatedComment.getCommentary());
		return comment;
	}

	@Override
	public void deleteComment(Long commentId) {
		Comment comment = this.commentRepository.findById(commentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found: " + commentId));
		this.commentRepository.delete(comment);
	}

}
