package com.example.demo.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.domain.LabUser;
import com.example.demo.domain.UserRole;
import com.example.demo.repository.LabUserRepository;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LabUserDetailsService implements UserDetailsService {

    private final LabUserRepository labUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LabUser labUser = labUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        UserRole role = "admin".equals(username) ? UserRole.ADMIN : UserRole.USER;

        return User.builder()
                .username(labUser.getUsername())
                .password(labUser.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(role.getValue())))
                .build();
    }
}