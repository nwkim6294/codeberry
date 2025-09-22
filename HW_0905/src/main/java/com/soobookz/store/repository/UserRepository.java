package com.soobookz.store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soobookz.store.domain.User;

public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByUsername(String username);
}
