package com.example.test.oauth2;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.test.domain.ClubUser;
import com.example.test.repository.ClubUserRepository;
import com.example.test.service.ClubUserRegistrationService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	
	private final ClubUserRepository clubUserRepository;
	private final ClubUserRegistrationService registrationService;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// TODO Auto-generated method stub
		ClubUser clubUser = null;
		Object principal = authentication.getPrincipal();

		// 1. Principal이 CustomOAuth2User 타입인지 먼저 확인
		if (principal instanceof CustomOAuth2User) {

			clubUser = ((CustomOAuth2User) principal).getClubUser();
		}
		// 2. 이 조건문을 실행하는 경우는 사용자 생성 후 DB 저장이 진행될거임
		//  -> 즉 처음 로그인한 사람의 정보를 처리할때는 여기를 탈것
		else if (principal instanceof OAuth2User) {
			
			OAuth2User oauth2User = (OAuth2User) principal;
			OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
			String registId = authToken.getAuthorizedClientRegistrationId();
			
			// SocialUserInfo 생성
			// 사용자 정보 객체 생성
			// OAuth2User 인터페이스를 통해 속성을 호출하면
			// Google의 경우는 GoogleUserInfo 인스턴스 생성
			// Kakao의 경우는 KakaoUserInfo 인인스턴스 생성
			SocialUserInfo socialUserInfo = SocialUserInfoFactory.getSocialUserInfo(registId, oauth2User.getAttributes());
			String socialId = registId + "_" + socialUserInfo.getId();
			
			// DB에서 조회 해본 후 없으면 생성
	        Optional<ClubUser> existingUser = clubUserRepository.findBySocialId(socialId);
			
			if(existingUser.isEmpty()) {
				clubUser = registrationService.saveOrUpdateSocialMember(socialUserInfo, registId);
	        } else {
	            clubUser = existingUser.get();
	        }
			
		} else {
			throw new ServletException("지원하지 않는 Principal 타입입니다. : " + principal.getClass().getName());
		}
		
		if(clubUser == null) {
			throw new ServletException("뭔가 잘못됨");
		}
		
		System.out.println("소셜 로그인 사용자 : "+ clubUser.getSocialProvider());
		
		// 권한 기반 리다이렉트
		String targetUrl = "/club/main";
		
	    if (clubUser.getRoles().contains(ClubUser.ClubRole.ROLE_ADMIN)) {
	        targetUrl = "/club/admin/dashboard";
	    } else if (clubUser.getRoles().contains(ClubUser.ClubRole.ROLE_VIP)) {
	        targetUrl = "/club/vip/lounge";
	    }
	   
	    response.sendRedirect(targetUrl);
	    
	}

}
