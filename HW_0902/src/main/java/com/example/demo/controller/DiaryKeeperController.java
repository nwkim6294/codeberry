package com.example.demo.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.domain.DiaryKeeper;
import com.example.demo.repository.DiaryKeeperRepository;

@Controller
@RequestMapping("/keepers")
public class DiaryKeeperController {
	
    private final DiaryKeeperRepository keeperRepository;
    private final PasswordEncoder passwordEncoder;
	
	public DiaryKeeperController(DiaryKeeperRepository keeperRepository, PasswordEncoder passwordEncoder) {
		this.keeperRepository = keeperRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	@GetMapping("/signup")
	public String showSignupForm(Model model) {
		model.addAttribute("keeper", new DiaryKeeper());
		return "users/signup-form";
	}
	
	@PostMapping("/signup")
	public String signup(@ModelAttribute DiaryKeeper keeper) {
		keeper.setPassword(passwordEncoder.encode(keeper.getPassword()));
		keeperRepository.save(keeper);
		return "redirect:/keepers/login";
	}
	
	@GetMapping("/login")
	public String showLoginForm() {
		return "users/login-form";
	}
}
