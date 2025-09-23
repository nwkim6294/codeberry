package com.example.test.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.test.domain.ClubUser;
import com.example.test.oauth2.CustomOAuth2User;
import com.example.test.oauth2.SocialUserInfo;
import com.example.test.oauth2.SocialUserInfoFactory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	
	private final ClubUserRegistrationService registrationService;

	// OAuth2UserService 소셜 로그인 구현 시 사용자 정보를 가져오고 처리하는 핵심 인터페이스
	// 파라미터 2개를 받는 것을 원칙으로함(무조건)

	// OAuth2UserRequest : OAuth2 공급자로부터 받은 엑세스 토큰과 클라이언트 정보를 포함하는 요청과 관련된 객체
	// OAuth2User : OAuth2 공급자로부터 가져온 인증된 사용자의 정보를 나타내는 객첼
	// 	-> 사용자의 이름, 이메일 등의 속성들을 포함
	// 구현하면 할 수 있는 것들
	// 1. OAuth2 공급자로부터 사용자 정보 로드
	// 2. 사용자 정보 변환 및 커스텀마이징
	// 3. 추가적인 사용자 정보 처리
	// 4. 데이터베이스와의 연동

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		System.out.println("새로운 소셜 친구 로그인");
		
		
		// DefaultOAuth2UserService : OAuth2에서 제공하는 기본 구현체
		// OAuth2 공급자로부터 사용자 정보를 가져오는 기본적인 로직 구현
		// OAuth2UserService 인터페이스 표준 구현체
		// REST 통신을 통해 OAuth2 공급자의 유저 정보 엔드포인트로부터 사용자 정보를 종회
		
		// 커스터마이징이 필요한 경우
		// 1. 소셜 로그인 후에 추가적인 사용자 정보가 필요한 경우
		// 2. 데이터베이스에 사용자 정보를 저장해야하는 경우
		// 3. 서로 다른 OAuth2 공급자의 응답형식을 통일해야하는 경우
		DefaultOAuth2UserService defaultService = new DefaultOAuth2UserService();
		// 사용자 정보를 요청하기 위해 우선 로딩을 진행
		// 1. OAuth2 제공자에 대한 정보와 엑세스 토큰을 발급 받음
		// 2. 제공자의 사용자 정보 엔드포인트로 HTTP 요청을 보냄
		// 3. 응답으로 사용자 정보를 map 타입으로 받음
		// 4. 받은 정보를 파당으로 사용자 권한을 설정하고
		//  -> DefaultOAuth2UserService 객체를 생성하여 리턴
		OAuth2User oauth2User = defaultService.loadUser(userRequest);
	
		// 사용자 속성 접근 예시
		//String email = oauth2User.getAttribute("email");
		
		String registId = userRequest.getClientRegistration().getRegistrationId();
		// getRegistrationId : 현재 로그인을 시도하는 OAuth2 서비스 제공자를 식별하는 고유한 ID
		//  -> 구글인지, 카카오인지, 네이버인지 구분
		
		String userNameAttrName = userRequest.getClientRegistration().getProviderDetails()
											 .getUserInfoEndpoint().getUserNameAttributeName();
		// getUserNameAttributeName : OAuth2 공급자가 사용자를 식별하는 키 값
		// Google : "sub"
		// Kakao : "id"
		// Naver : "response"
		
		System.out.println("어느 플랫폼에서 왔는가?" + registId);
		
		// 3. 소셜 사용자 정보 통합 처리
        SocialUserInfo socialUserInfo = SocialUserInfoFactory.getSocialUserInfo(
                registId, oauth2User.getAttributes()
            );
		
		// 4. 회원 정보 저장 또는 업데이트
        ClubUser clubUser = registrationService.saveOrUpdateSocialMember(socialUserInfo, registId);
		
		// 5. CustomOAuth2User 객체를 생성한 후 리턴
        Set<SimpleGrantedAuthority> authorities = clubUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
        
        return new CustomOAuth2User(
                authorities,
                oauth2User.getAttributes(),
                userNameAttrName,
                clubUser
            );
        
	}

}
