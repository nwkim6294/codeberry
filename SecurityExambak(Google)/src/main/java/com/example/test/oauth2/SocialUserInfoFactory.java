package com.example.test.oauth2;

import java.util.Map;

public class SocialUserInfoFactory {

	// 팩토리 디자인 패턴
	// 객체 생성 로직 자체를 캡슐화 하여 결합도를 낮추고 유연성을 높이는 패턴
	// 사용 이유 : 유지보수성을 높이기 위해
	//  -> 기존 코드 수정 없이 새로운 구현체를 만들기 편하다
	//  -> 코드의 재사용성 증대
	//  -> 테스트 시 굉장히 용이
	
	public static SocialUserInfo getSocialUserInfo(String registId, Map<String, Object> attributes) {
		
		switch (registId.toLowerCase()) {
			case "google": {
				return new GoogleUserInfo(attributes);
				
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + registId);
			}
		
	}
}
