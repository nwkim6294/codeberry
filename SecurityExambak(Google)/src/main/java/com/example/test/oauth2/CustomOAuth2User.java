package com.example.test.oauth2;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.example.test.domain.ClubUser;

// ClubUser 엔티티를 포함하여 사용하기 쉽도록 만들기 위한 커스텀 Oauth2 객체
public class CustomOAuth2User implements OAuth2User{

    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final ClubUser clubUser;
	
	public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes,
			String nameAttributeKey, ClubUser clubUser) {
		this.authorities = authorities;
		this.attributes = attributes;
		this.nameAttributeKey = nameAttributeKey;
		this.clubUser = clubUser;
	}

	@Override
	public Map<String, Object> getAttributes() {
		// TODO Auto-generated method stub
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return authorities;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return (String) attributes.get(nameAttributeKey);
	}
	
    // 추가 메서드 -> 선택 사항
    public ClubUser getClubUser() {
        return clubUser;
    }

    public String getEmail() {
        return clubUser.getEmail();
    }

    public String getNickname() {
        return clubUser.getNickname();
    }

    public String getProfileImageUrl() {
        return clubUser.getProfileImageUrl();
    }
	
}
