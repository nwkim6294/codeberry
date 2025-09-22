package com.flex.challenge.repository;

import com.flex.challenge.domain.FlexPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlexPostRepository extends JpaRepository<FlexPost, Long> {
    Page<FlexPost> findByTitleContaining(String title, Pageable pageable);
}
