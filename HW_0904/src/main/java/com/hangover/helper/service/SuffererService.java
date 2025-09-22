package com.hangover.helper.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hangover.helper.domain.Sufferer;
import com.hangover.helper.repository.SuffererRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuffererService {
	private final SuffererRepository suffererRepository;
	private final PasswordEncoder passwordEncoder;
	
	@Transactional
	public void signup(Sufferer sufferer) {
		Sufferer newUser = Sufferer.builder()
				.username(sufferer.getUsername())
				.password(passwordEncoder.encode(sufferer.getPassword()))
				.build();
		suffererRepository.save(newUser);
	}


}
