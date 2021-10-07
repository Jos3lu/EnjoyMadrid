package com.enjoymadrid.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enjoymadrid.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {	
}
