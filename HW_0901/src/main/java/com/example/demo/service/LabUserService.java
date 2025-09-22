package com.example.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.LabUser;
import com.example.demo.repository.LabUserRepository;

import lombok.RequiredArgsConstructor;

// UserService를 LabUserService로 변경하고, LabUser 엔티티를 사용하도록 수정하세요.
@Service
@RequiredArgsConstructor
@Transactional
public class LabUserService {
	private final LabUserRepository labUserRepository;
	private final PasswordEncoder passwordEncoder;
	
	public void signup(LabUser labUser) {
		
		// builder api 쓰는법 헷갈릴거 같아서 수정함
//		LabUser newLabUser = LabUser.builder()
//				.username(labUser.getUsername())
//				.password(passwordEncoder.encode(labUser.getPassword()))
//				.build();
//		labUserRepository.save(newLabUser);
		
		LabUser newLabUser = new LabUser();
		newLabUser.setUsername(labUser.getUsername());
		newLabUser.setPassword(passwordEncoder.encode(labUser.getPassword()));
		
		labUserRepository.save(newLabUser);
	}

}
