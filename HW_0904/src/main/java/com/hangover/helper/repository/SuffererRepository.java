package com.hangover.helper.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hangover.helper.domain.Sufferer;

public interface SuffererRepository extends JpaRepository<Sufferer, Long> {
	Optional<Sufferer> findByUsername(String username);
}
