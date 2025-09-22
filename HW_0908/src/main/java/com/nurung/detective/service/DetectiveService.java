package com.nurung.detective.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nurung.detective.domain.Detective;
import com.nurung.detective.dto.DetectiveDto;
import com.nurung.detective.repository.DetectiveRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DetectiveService {

	private final DetectiveRepository detectiveRepository;
	private final PasswordEncoder passwordEncoder;
	
	@Transactional
	public void signup(DetectiveDto detectiveDto) {
		if(detectiveRepository.findByUsername(detectiveDto.getUsername()).isPresent()) {
			throw new IllegalArgumentException("이미 존재하는 아이디입니다 : " + detectiveDto.getUsername());
		}
		
		
		Detective newUser = Detective.builder()
				.username(detectiveDto.getUsername())
				.password(passwordEncoder.encode(detectiveDto.getPassword()))
				.build();
		
		detectiveRepository.save(newUser);
		
	}
	

}
