package com.flex.challenge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.flex.challenge.dto.UserDto;
import com.flex.challenge.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
	
	private final UserService userService;
	
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("user", new UserDto());
		return "users/signup-form";
	}
	
	@PostMapping("/signup")
	public String signup(@ModelAttribute("user") UserDto userDto) {
		userService.signup(userDto);
		return "redirect:/users/login";
	}
	
	@GetMapping("/login")
	public String login(Model model) {
		return "users/login-form";		
	}

}
