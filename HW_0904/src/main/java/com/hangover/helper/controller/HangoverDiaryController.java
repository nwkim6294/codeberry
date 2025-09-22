package com.hangover.helper.controller;

import com.hangover.helper.domain.HangoverDiary;
import com.hangover.helper.form.HangoverDiaryForm;
import com.hangover.helper.service.HangoverDiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.Principal;


@Controller
@RequiredArgsConstructor
@RequestMapping("/diaries")
public class HangoverDiaryController {
	private final HangoverDiaryService diaryService;
	
	@GetMapping
	public String myDiaries(Model model, Principal principal) {
		model.addAttribute("diaries", diaryService.findMyDiaries(principal.getName()));
		return "diaries/list";
	}
	
	@GetMapping("/new")
	public String newDiaryForm(Model model) {
		model.addAttribute("diary", new HangoverDiaryForm());
		return "diaries/new-form";
	}
	
	@PostMapping("/new")
	public String createDiary(@ModelAttribute("form") HangoverDiaryForm form,
			@RequestParam("file") MultipartFile file, Principal principal) throws IOException {
		diaryService.saveDiary(form, file, principal.getName());
		return "redirect:/diaries";
	}

	@GetMapping("/{id}/edit")
	public String editDiaryForm(@PathVariable Long id, Model model, Principal principal) {
		try {
			HangoverDiary diary = diaryService.findByIdAndValidateOwnership(id, principal.getName());
			model.addAttribute("diary", diary);
			return "diaries/edit-form";
		} catch (AccessDeniedException e) {
			return "redirect:/diaries";
		}
	}
	
	@PostMapping("/{id}/edit")
	public String updateDiary(@PathVariable Long id, 
			@ModelAttribute("form") HangoverDiaryForm form, Principal principal) {
		diaryService.updateDiary(id, form, principal.getName());
		return "redirect:/diaries";
	}
	
	@PostMapping("/{id}/delete")
	public String deleteDiary(@PathVariable Long id, Principal principal) {
		diaryService.deleteDiary(id, principal.getName());
		return "redirect:/diaries";
	}
 }
