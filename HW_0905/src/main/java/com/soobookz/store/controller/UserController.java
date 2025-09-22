package com.soobookz.store.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soobookz.store.form.UserForm;
import com.soobookz.store.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
	
	private final UserService userService;
	
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("user", new UserForm());
		return "users/signup-form";
	}
	
	@PostMapping("/signup")
	public String signup(@ModelAttribute UserForm userForm) {
		userService.signup(userForm);
		return "redirect:/users/login";
	}
	
	@GetMapping("/login")
	public String loginFomr() {
		return "users/login-form";
	}
	
}