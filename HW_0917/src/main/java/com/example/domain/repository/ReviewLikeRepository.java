package com.example.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.domain.entity.Member;
import com.example.domain.entity.Review;
import com.example.domain.entity.ReviewLike;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long>{
	Optional<ReviewLike> findByMemberAndReview(Member member, Review review);
    long countByReview(Review review);
}
