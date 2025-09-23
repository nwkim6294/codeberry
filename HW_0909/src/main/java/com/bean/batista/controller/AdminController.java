package com.bean.batista.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bean.batista.dto.MissionDto;
import com.bean.batista.repository.AgentRepository;
import com.bean.batista.service.AgentService;
import com.bean.batista.service.MissionService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
	
	private final AgentService agentService;
	private final AgentRepository agentRepository;
	private final MissionService missionService;
	
	@GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("missions", missionService.findAllMissions());
        return "admin/dashboard";
    }
	
	@GetMapping("/missions/new")
	public String newMissionForm(Model model) {
		model.addAttribute("mission", new MissionDto());
		model.addAttribute("agents", agentRepository.findAll());
		return "admin/mission-form";
	}
	
	@PostMapping("/missions/new")
	public String createMission(@ModelAttribute("dto") MissionDto dto, @RequestParam("agentId") Long agentId) {
		missionService.createMission(dto, agentId);
		return "redirect:/admin/dashboard";
	}

}
