package com.bean.batista.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bean.batista.domain.Agent;

public interface AgentRepository extends JpaRepository<Agent, Long>{
	Optional<Agent> findByUsername(String username);
}
