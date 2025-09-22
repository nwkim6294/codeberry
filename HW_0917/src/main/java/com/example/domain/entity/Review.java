package com.example.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 200, nullable = false)
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Book book;
    
    @Builder
    public Review(String content, Member member, Book book) {
    	this.content = content;
    	this.member = member;
    	this.book = book;
    }
	
}
