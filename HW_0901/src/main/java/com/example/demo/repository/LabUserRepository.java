package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.LabUser;

public interface LabUserRepository extends JpaRepository<LabUser, Long>{
	Optional<LabUser> findByUsername(String username);

}
