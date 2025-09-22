package com.example.demo;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/memories")
public class MemoryController {
	
	private final MemoryService memoryService;	
	
	@GetMapping
	public String readList(Model model) {
        model.addAttribute("memories", memoryService.findAll());
        return "memories/list";
	}
	
	// new
	@GetMapping("/new")
	public String newForm(Model model) {
		model.addAttribute("memory", new Memory());
		return "memories/new-form";
	}
	
	@PostMapping("/new")
	public String create(@ModelAttribute Memory memory) {
		memoryService.save(memory);
		return "redirect:/memories";
	}
	
	// edit
	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable("id") Long id, Model model) {
		Memory memory = this.memoryService.findById(id);
		model.addAttribute("memory", memory);
		return "memories/edit-form";
	}
	
	@PostMapping("/{id}/edit")
	public String update(@PathVariable("id") Long id, @ModelAttribute Memory memory) {
		memoryService.update(id, memory);  
		return "redirect:/memories";
	}
	
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable("id") Long id) {
	    memoryService.delete(id);
	    return "redirect:/memories";
	}

}
