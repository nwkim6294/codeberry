package com.example.demo.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.domain.DiaryKeeper;
import com.example.demo.repository.DiaryKeeperRepository;

@Service
public class KeeperDetailsService implements UserDetailsService{
	
	private final DiaryKeeperRepository keeperRepository;
	
	public KeeperDetailsService(DiaryKeeperRepository keeperRepository) {
		this.keeperRepository = keeperRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 람다식으로 표현하는 방법
        // DiaryKeeper keeper = keeperRepository.findByUsername(username)
        //        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
		
		Optional<DiaryKeeper> keeperOptional = keeperRepository.findByUsername(username);
		if(!keeperOptional.isPresent()) {
			throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
		}
		
		DiaryKeeper keeper = keeperOptional.get();
		return new User(keeper.getUsername(), keeper.getPassword(), Collections.emptyList());
	}
}
