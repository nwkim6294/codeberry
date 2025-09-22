package com.example.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.domain.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	
}
