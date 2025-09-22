package com.soobookz.store.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soobookz.store.domain.User;
import com.soobookz.store.domain.UserRole;
import com.soobookz.store.form.UserForm;
import com.soobookz.store.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	@Transactional
	public void signup(UserForm userForm) {
		if(userRepository.findByUsername(userForm.getUsername()).isPresent()) {
			throw new IllegalArgumentException("이미 존재하는 아이디입니다: " + userForm.getUsername());
		}
		
		User newUser = User.builder()
				.username(userForm.getUsername())
				.password(passwordEncoder.encode(userForm.getPassword()))
				.role(UserRole.ADMIN)
				.build();
		userRepository.save(newUser);
	}

}
