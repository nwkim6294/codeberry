package com.flex.challenge.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flex.challenge.dto.CommentDto;
import com.flex.challenge.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentApiController {

	private final CommentService commentService;
	
	@GetMapping
	public ResponseEntity<List<CommentDto>> getComments(@PathVariable("postId") Long postId) {
        List<CommentDto> comments = commentService.findCommentsByPostId(postId);
		return ResponseEntity.ok(comments);
	}
	
	@PostMapping
	public ResponseEntity<CommentDto> createComment(@PathVariable("postId") Long postId, @RequestBody Map<String, String> payload, Principal principal) {
		String content = payload.get("content");
		CommentDto newComment = commentService.createComment(postId, content, principal.getName());
		return ResponseEntity.ok(newComment);
	}
	
}
