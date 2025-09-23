package com.bean.batista.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bean.batista.dto.AgentDto;
import com.bean.batista.service.AgentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class AgentController {
	private final AgentService agentService;
	
	@GetMapping("/signup")
	public String signupForm(Model model) {
		model.addAttribute("agent", new AgentDto());
		return "users/signup-form";
	}
	
	@PostMapping("/signup")
	public String signup(@ModelAttribute("agent") AgentDto dto) {
		agentService.signup(dto);
		return "redirect:/users/login";
	}
	
	@GetMapping("/login")
	public String loginForm() {
		return "users/login-form";
	}
}
