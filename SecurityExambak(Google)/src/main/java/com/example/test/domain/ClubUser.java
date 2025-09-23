package com.example.test.domain;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name =  "club_users")
public class ClubUser {

	// Role 열거형
	public static enum ClubRole {
		ROLE_USER, ROLE_VIP, ROLE_ADMIN
	}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String nickname;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "club_user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<ClubRole> roles;
    
    @Column(nullable = false)
    private boolean enabled = true;
    
    @Column(nullable = false)
    private boolean accountNonExpired = true;
    
    @Column(nullable = false)
    private boolean accountNonLocked = true;
    
    @Column(nullable = false)
    private boolean credentialsNonExpired = true;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime lastLoginAt;
    
    // --- 소셜 로그인 정보 ---
    @Column(length = 50)
    private String socialProvider; // 예: "google", "kakao"

    @Column(unique = true)
    private String socialId; // 소셜 프로바이더가 제공하는 고유 ID

    @Column
    private String profileImageUrl; // 프로필 이미지 URL
    
    // 기본 생성자
    public ClubUser() {}
    
    // 생성자
    public ClubUser(String username, String email, String password, String nickname, Set<ClubRole> roles) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.roles = roles;
    }
    
    /**
     * 소셜 로그인 시 기존 회원 정보 업데이트
     */
    public void updateSocialInfo(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
    
    
}
