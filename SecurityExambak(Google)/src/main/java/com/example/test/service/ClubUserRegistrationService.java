package com.example.test.service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.test.domain.ClubUser;
import com.example.test.oauth2.SocialUserInfo;
import com.example.test.repository.ClubUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClubUserRegistrationService {
	
	private final ClubUserRepository clubUserRepository;
	private final PasswordEncoder passwordEncoder;
	
	// 항상 새로운 트랜잭션을 생성하도록 지정하는 어노테이션 설정
	// 동작 방식 
	// 1. 기존 트랜잭션의 존재 여부와는 관계없이 새로운 트랜잭션을 생성
	// 2. 이미 실행중인 트랜잭션이 있다면 해당 트랜잭션을 일시 중단
	// 3. 새로운 트랜잭션 생성
	// 4. 새 트랜잭션이 완료된 후 기존 트랜잭션을 재개
	
	// 주요 특징 
	// 독립적인 트랜잭션을 보장
	// 대신 꼭 필요한 경우에만 사용(중요한 프로세스에만 써야함)
	
	// REQUIRES_NEW를 대체
	// 1. REQUIRES : 기존 트랜잭션 재사용
	// 2. SUPPORTS : 트랜잭션이 있으면 사용 없으면 없이 실행
	// 3. MANDATORY : 기존 트랜잭셩 필수
	// 4. NOT_SUPPORTED : 트랜잭션 없이 실행 
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ClubUser saveOrUpdateSocialMember(SocialUserInfo socialUserInfo, String provider) {
		
		// 소셜 ID 생성 및 사용자 조회
		// 소셜 제공자 + 사용자 ID의 조합으로 새로운 아이디 생성
		// ex) google_nureongi
		String socialId = provider + "_" + socialUserInfo.getId();
		
		// 기존 사용자인지 아닌지 구분할 필요 있음
		Optional<ClubUser> existingUserOpt = clubUserRepository.findBySocialId(socialId);
		// 기존 사용자가 아니면 신규 사용자 등록 알고리즘 동작  
		if(existingUserOpt.isPresent()) {
			System.out.println("기존 정보 업데이트 처리");
			ClubUser existingUser = existingUserOpt.get();
			existingUser.updateSocialInfo(provider, socialId);
			return clubUserRepository.save(existingUser);
		} else {
            ClubUser newUser = new ClubUser();
            newUser.setUsername(generateUniqueUsername(socialUserInfo.getEmail()));
            newUser.setEmail(socialUserInfo.getEmail() != null ? socialUserInfo.getEmail() : generateTempEmail(socialUserInfo.getName()));
            newUser.setNickname(socialUserInfo.getName());
            newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            newUser.setRoles(Collections.singleton(ClubUser.ClubRole.ROLE_USER));
            newUser.setSocialId(socialId);
            newUser.setSocialProvider(provider);
            newUser.setProfileImageUrl(socialUserInfo.getProfileImageUrl());
            newUser.setEnabled(true);
            return clubUserRepository.save(newUser);
		}
		
	}

	private String generateTempEmail(String name) {
		// TODO Auto-generated method stub
        return name.replaceAll("\\s+", "") + "_" + UUID.randomUUID().toString().substring(0, 8) + "@social.user.temp";
	}

	private String generateUniqueUsername(String email) {
		// TODO Auto-generated method stub
		
		if(email == null) {
			// 이메일이 null 값이라면 임의의 사용자명을 생성
            return "social_user_" + UUID.randomUUID().toString().substring(0, 8);
		}
		
		String baseUsername = email.split("@")[0];
		String username = baseUsername;
		int cnt = 1;
		
		while (clubUserRepository.existsByUsername(username)) {
			// DB에서 사용자명 중복 확인
			// 중복 발생 시 숫자를 증가시켜 새로운 사용자명 생성
            username = baseUsername + "_" + cnt++;
        }
		
		return username;
	}
	
}
