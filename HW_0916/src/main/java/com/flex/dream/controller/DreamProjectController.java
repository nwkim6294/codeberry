package com.flex.dream.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.flex.dream.service.DreamProjectService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class DreamProjectController {
	private final DreamProjectService projectService;
	
	@GetMapping("/workshop")
	public String workshopPage(Model model) {
		model.addAttribute("unassignedFragments", projectService.findUnassignedFragments());
	    return "workshop";
	}
	
	
    @PostMapping("/workshop/find-fragments")
    public String refreshFragments(Model model) {
        model.addAttribute("unassignedFragments", projectService.findUnassignedFragments());
        return "workshop";
    }
}
