package com.example.test.oauth2;

import java.util.Map;

public class KaKaoUserInfo implements SocialUserInfo {
	
	private final Map<String, Object> attributes;
	
	public KaKaoUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

    @Override
    public String getId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    @SuppressWarnings("unchecked") // 경고나면 체크 안한다
    public String getName() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        if (properties == null) return "카카오 사용자";
        return (String) properties.get("nickname");
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) return null;
        return (String) kakaoAccount.get("email");
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getProfileImageUrl() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        if (properties == null) return null;
        return (String) properties.get("profile_image");
    }
}
