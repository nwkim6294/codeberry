package com.flex.challenge.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class CommentDto {
    private Long id;
    private String content;
    private String username;
    private String createdAt;

    public CommentDto(Long id, String content, String username, java.time.LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.username = username;
        this.createdAt = createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}