package com.example.test.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ClubMainController {

//	private final ClubUserDetailService userDetailService;
	
    @GetMapping("/club/main")
    public String mainPage(Authentication authentication, Model model) {
        System.out.println("메인 페이지 접근: " + authentication.getName());
        
        // 사용자 정보 추가
        model.addAttribute("username", authentication.getName());
        model.addAttribute("authorities", authentication.getAuthorities());
        
        return "main";
    }
    
    @GetMapping("/club/lounge")
    public String loungePage() {
        System.out.println("일반 라운지 접근");
        return "lounge";
    }
    
    @GetMapping("/club/games")
    public String gamesPage() {
        System.out.println("게임 페이지 접근");
        return "games";
    }
    
    @GetMapping("/club/counseling")
    public String counselingPage() {
        System.out.println("상담 페이지 접근");
        return "counseling";
    }
    
    @GetMapping("/club/profile")
    public String profilePage(Authentication authentication, Model model) {
        System.out.println("프로필 페이지 접근: " + authentication.getName());
        
        // 사용자 정보 조회
//        var user = userDetailsService.findByUsername(authentication.getName());
//        model.addAttribute("user", user);
        
        return "profile";
    }
}