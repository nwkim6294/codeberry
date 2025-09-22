package com.nurung.detective.controller;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.nurung.detective.dto.CaseFileRequestDto;
import com.nurung.detective.dto.CaseFileResponseDto;
import com.nurung.detective.service.CaseFileService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cases")
public class CaseFileController {
	
	private final CaseFileService caseFileService;
	
	@GetMapping("/my-cases")
	public String myCases(Model model, Principal principal) {
		model.addAttribute("cases", caseFileService.findMyCases(principal.getName()));
		return "cases/my-list";
	}
	
	@GetMapping("/archive")
	public String archive(Model model) {
		model.addAttribute("cases", caseFileService.findClosedCases());
		return "cases/archive-list";
	}
	
	@GetMapping("/new")
	public String newCaseForm(Model model) {
		model.addAttribute("caseFile", new CaseFileRequestDto());
		return "cases/new-form";
	}
	
	@PostMapping("/new")
	public String create(@ModelAttribute CaseFileRequestDto caseFileDto,
			@RequestParam("file") MultipartFile file, 
			Principal principal) throws IOException {
		caseFileService.createCase(caseFileDto, file, principal.getName());
		return "redirect:/cases/my-cases";
	}
	
	@GetMapping("/{id}/edit")
	public String editCaseForm(@PathVariable("id") Long id, Model model, Principal principal) {
		try {
            model.addAttribute("caseFile", caseFileService.findByIdAndValidateOwnership(id, principal.getName()));
			return "cases/edit-form";
        } catch (Exception e) {
			return "redirect:/cases/my-cases";
		}
	}
	
	@PostMapping("/{id}/edit")
	public String updateCase(@PathVariable("id") Long id, 
			@ModelAttribute CaseFileRequestDto caseFileDto, 
			Principal principal) {
		try {
			caseFileService.updateCase(id, caseFileDto, principal.getName());
        } catch (IllegalArgumentException | IllegalStateException e) {
			return "redirect:/cases/my-cases";
		}
		return "redirect:/cases/my-cases";
	}
	
	@PostMapping("/{id}/close")
	public String closeCase(@PathVariable("id") Long id, Principal principal) {
		caseFileService.closeCase(id, principal.getName());
		return "redirect:/cases/my-cases";
	}
}
