package com.nurung.detective.service;

import java.util.Collections;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nurung.detective.domain.Detective;
import com.nurung.detective.repository.DetectiveRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DetectiveDetailsService implements UserDetailsService {
    private final DetectiveRepository detectiveRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Detective user = detectiveRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 탐지견을 찾을 수 없습니다: " + username));
        return new User(user.getUsername(), user.getPassword(), Collections.emptyList());
    }
}
