package com.bean.batista.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bean.batista.domain.Agent;
import com.bean.batista.repository.AgentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgentDetailsService implements UserDetailsService {

	private final AgentRepository agentRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
        Agent agent = agentRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 요원을 찾을 수 없습니다: " + username));
		
        return User.builder()
                .username(agent.getUsername())
                .password(agent.getPassword())
                .roles(agent.getRole().name())
                .build();
	}
	

}
