package com.flex.dream.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flex.dream.entity.DreamProject;

public interface DreamProjectRepository extends JpaRepository<DreamProject, Long>{
    boolean existsByActivationCode(String activationCode);
}
