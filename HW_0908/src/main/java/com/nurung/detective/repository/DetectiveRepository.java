package com.nurung.detective.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nurung.detective.domain.Detective;

public interface DetectiveRepository extends JpaRepository<Detective, Long>{
	Optional<Detective> findByUsername(String username);
	
}
