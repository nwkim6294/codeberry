package com.hangover.helper.service;

import java.util.Collections;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hangover.helper.domain.Sufferer;
import com.hangover.helper.repository.SuffererRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuffererDetailsService implements UserDetailsService {
	private final SuffererRepository suffererRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Sufferer user = suffererRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
		return new User(user.getUsername(), user.getPassword(), Collections.emptyList());
	}

}
