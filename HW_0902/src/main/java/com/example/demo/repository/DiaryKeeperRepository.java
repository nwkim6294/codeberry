package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.DiaryKeeper;

public interface DiaryKeeperRepository extends JpaRepository<DiaryKeeper, Long>{
	Optional<DiaryKeeper> findByUsername(String username);
}
