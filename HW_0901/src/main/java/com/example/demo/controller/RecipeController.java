package com.example.demo.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.domain.Recipe;
import com.example.demo.service.RecipeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/recipes")
public class RecipeController {
	
	private final RecipeService recipeService;
	
	@GetMapping
	public String readList(Model model) {
		model.addAttribute("recipes", recipeService.findAll());
		return "recipes/list";
	}
	
	// 새로만들기
	@GetMapping("/new")
	public String create(Model model) {
        model.addAttribute("recipe", new Recipe());
        return "recipes/new-form";
	}
	
	@PostMapping("/new")
	public String create(@RequestParam("name") String name, 
						 @RequestParam("description") String description, Principal principal) {
		Recipe recipe = Recipe.builder()
				.name(name)
				.description(description)
				.build();
		recipeService.save(recipe, principal.getName());
		return "redirect:/recipes";
	}
	
	// 상세보기 수정
	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable("id") Long id, Model model, Principal principal) {
		Recipe recipe = recipeService.findById(id);
		// 소유권 검사 로직을 labUser 기준으로 변경하세요.
		if(!recipe.getLabUser().getUsername().equals(principal.getName())) {
			return "redirect:/edit-form";
		}
		model.addAttribute("recipe", recipe);
		return "recipes/edit-form";
	}
	// 위에는 @RequestParam을 썼고
	// 여기는 @ModelAttribute로 한번에 받아왔는데
	// 차이는 Recipe 엔티티에 @Setter가 있기 때문에 가
	@PostMapping("/{id}/edit")
	public String update(@PathVariable("id") Long id, @ModelAttribute("recipe") Recipe recipe, Principal principal) {
		recipeService.update(id, recipe, principal.getName());
		return "redirect:/recipes";
	}
	
	// 삭제
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable("id") Long id, Principal principal) {
		recipeService.delete(id, principal.getName());
		return "redirect:/recipes";
	}
}
