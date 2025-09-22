package com.hangover.helper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.hangover.helper.domain.Sufferer;
import com.hangover.helper.service.SuffererService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class SuffererController {
	private final SuffererService suffererService;
	
	@GetMapping("/signup")
	public String signupForm(Model model) {
		model.addAttribute("user", new Sufferer());
		return "users/signup-form";
	}
	
	@PostMapping("/signup")
	public String signup(@ModelAttribute("user") Sufferer user) {
		suffererService.signup(user);
		return "redirect:/users/login";
	}
	
    @GetMapping("/login")
    public String loginForm() {
        return "users/login-form";
    }

}
