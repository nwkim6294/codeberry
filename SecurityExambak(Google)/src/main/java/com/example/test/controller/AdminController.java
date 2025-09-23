package com.example.test.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.test.repository.ClubUserRepository;
import com.example.test.service.ClubUserService;

@Controller
public class AdminController {
    
    private final ClubUserRepository userRepository;
    private final ClubUserService userService;
    
    public AdminController(ClubUserRepository userRepository, ClubUserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }
    
    @GetMapping("/club/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard(Model model) {
        System.out.println("🔧 관리자 대시보드 접근");
        
        // 사용자 통계
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("activeUsers", userRepository.countByEnabledTrue());
        
        return "admin-dashboard";
    }
    
    @GetMapping("/club/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboardAlias(Model model) {
        return adminDashboard(model);
    }
    
    @PostMapping("/club/admin/promote-vip")
    @PreAuthorize("hasRole('ADMIN')")
    public String promoteToVip(@RequestParam String username, Model model) {
        try {
            userService.promoteToVip(username);
            model.addAttribute("success", username + "님을 VIP로 승급시켰습니다!");
        } catch (Exception e) {
            model.addAttribute("error", "승급 실패: " + e.getMessage());
        }
        return "redirect:/club/admin";
    }
}