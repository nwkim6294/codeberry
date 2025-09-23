package com.bean.batista.controller;

import java.io.IOException;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.bean.batista.service.AgentService;
import com.bean.batista.service.MissionService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/missions")
@RequiredArgsConstructor
public class MissionController {
	
	private final MissionService missionService;

	@GetMapping("/my-missions")
	public String myMissions(Model model, Principal principal) {
		model.addAttribute("missions", missionService.findMyMissions(principal.getName()));
		return "missions/my-list";
	}

	@GetMapping("/{id}/complete")
	public String completeForm(@PathVariable("id") Long id, 
						       Model model, Principal principal) {
		try {
			model.addAttribute("mission", missionService.findByIdAndValidateOwnerShip(id, principal.getName()));
			return "missions/complete-form";
		} catch (Exception e) {
			return "redirect:/missions/my-missions";
		}
	}
	
	@PostMapping("/{id}/complete")
	public String complete(@PathVariable("id") Long id, 
						   @RequestParam("file") MultipartFile file, 
						   Principal principal) throws IOException {
		missionService.completeMission(id, file, principal.getName());
		return "redirect:/missions/my-missions";
	}
	
	
}
