package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.domain.LabUser;
import com.example.demo.service.LabUserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lab-users")
public class LabUserController {
	
	private final LabUserService labUserService;
	
	@GetMapping("/signup")
    public String signupForm(Model model) {
		LabUser l1 = new LabUser();
        model.addAttribute("labUser", l1);
        return "users/signup-form";
    }
	
	@PostMapping("/signup")
	public String signup(@ModelAttribute("labUser") LabUser labUser) {
		labUserService.signup(labUser);
		return "redirect:/lab-users/login";
	}
	
	@GetMapping("/login")
	public String loginForm() {
		return "users/login-form";
	}
	
}
