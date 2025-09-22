package com.flex.challenge.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flex.challenge.domain.Comment;
import com.flex.challenge.domain.FlexPost;
import com.flex.challenge.domain.User;
import com.flex.challenge.dto.CommentDto;
import com.flex.challenge.repository.CommentRepository;
import com.flex.challenge.repository.FlexPostRepository;
import com.flex.challenge.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
	private final CommentRepository commentRepository;
	private final FlexPostRepository postRepository;
	private final UserRepository userRepository;
	
	public List<CommentDto> findCommentsByPostId(Long postId) {
	    return commentRepository.findByFlexPost_IdOrderByCreatedAtAsc(postId)
	            .stream()
	            .map(comment -> new CommentDto(
	                    comment.getId(),
	                    comment.getContent(),
	                    comment.getUser() != null ? comment.getUser().getUsername() : "알 수 없음", // ✅ NPE 방지
	                    comment.getCreatedAt()))
	            .collect(Collectors.toList());
	}
	
	@Transactional
	public CommentDto createComment(Long postId, String content, String username) {
		FlexPost post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
		User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
		Comment comment = Comment.builder()
				.content(content)
				.flexPost(post)
				.user(user)
				.build();
		Comment savedComment = commentRepository.save(comment);
		return new CommentDto(
				savedComment.getId(), 
				savedComment.getContent(),
				savedComment.getUser().getUsername(),
				savedComment.getCreatedAt());
	}
	
}
