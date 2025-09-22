package com.flex.challenge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flex.challenge.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByFlexPost_IdOrderByCreatedAtAsc(Long postId);
}