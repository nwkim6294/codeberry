package com.bean.batista.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	@GetMapping("/")
	public String home(Principal principal) {
		if(principal != null) {
			return "redirect:/missions/my-missions";
		}
		return "home";
	}

}
