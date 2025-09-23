package com.bean.batista.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bean.batista.domain.Agent;
import com.bean.batista.domain.Mission;

public interface MissionRepository extends JpaRepository<Mission, Long>{
	List<Mission> findByAgent(Agent agent);
}
