package com.nurung.detective.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nurung.detective.dto.DetectiveDto;
import com.nurung.detective.service.DetectiveService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class DetectiveController {
	
	private final DetectiveService detectiveService;
	
	@GetMapping("/signup")
	public String signupForm(Model model) {
		model.addAttribute("user", new DetectiveDto());
		return "/users/signup-form";
	}
	
	@PostMapping("/signup")
	public String signup(@ModelAttribute("user") DetectiveDto detectiveDto) {
		detectiveService.signup(detectiveDto);
		return "redirect:/users/login";
	}
	
	@GetMapping("/login")
	public String loginForm() {
		return "users/login-form";
	}

}
