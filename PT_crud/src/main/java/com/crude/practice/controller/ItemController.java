package com.crude.practice.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.crude.practice.domain.MarketItem;
import com.crude.practice.service.ItemService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ItemController {
	private final ItemService itemService;
	
	@GetMapping("/")
	public String listForm(@RequestParam(required = false, value = "keword") String keyword, Model model) {
		List<MarketItem> items;
		if(keyword != null && !keyword.isEmpty()) {
			items = itemService.search(keyword);
		} else {
			items = itemService.findAll();
		}
		model.addAttribute("items", items);
		model.addAttribute("keyword", keyword);
		return "items/list";
	}
	
	@GetMapping("/items/new")
	public String newForm(Model model) {
		model.addAttribute("item", new MarketItem());
		return "items/new-form";
	}
	
	@PostMapping("/items/new")
	public String create(@ModelAttribute("item") MarketItem item) {
		itemService.create(item);
		return "redirect:/";
	}
	
	@GetMapping("/items/{id}/edit")
	public String editForm(@PathVariable("id") Long id, Model model) {
		MarketItem item = itemService.findById(id);
		model.addAttribute("item", item);
		return "items/edit-form";
	}
	
	@PostMapping("/items/{id}/edit")
	public String update(@PathVariable("id") Long id, @ModelAttribute("item") MarketItem item) {
		itemService.update(id, item);
		return "redirect:/";
	}
	
	@PostMapping("/items/{id}/delete")
	public String delete(@PathVariable("id") Long id) {
		itemService.delete(id);
		return "redirect:/";
	}
	
}

