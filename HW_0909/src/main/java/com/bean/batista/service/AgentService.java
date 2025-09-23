package com.bean.batista.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bean.batista.domain.Agent;
import com.bean.batista.dto.AgentDto;
import com.bean.batista.repository.AgentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgentService {
	
	private final AgentRepository agentRepository;
	private final PasswordEncoder passwordEncoder;
	
	
	public void signup(AgentDto dto) {
		if(agentRepository.findByUsername(dto.getUsername()).isPresent()) {
			throw new IllegalArgumentException("이미 존재하는 호출명(ID)입니다.");
		}
		

		Agent agent = Agent.builder()
				.username(dto.getUsername())
				.password(passwordEncoder.encode(dto.getPassword()))
				.role(dto.getRole())
				.build();
		
		agentRepository.save(agent);
	}

}
