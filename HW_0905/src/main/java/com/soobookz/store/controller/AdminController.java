package com.soobookz.store.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.soobookz.store.domain.Item;
import com.soobookz.store.domain.Store;
import com.soobookz.store.form.ItemForm;
import com.soobookz.store.form.StoreForm;
import com.soobookz.store.service.ItemService;
import com.soobookz.store.service.StoreService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final StoreService storeService;
	private final ItemService itemService;

	@GetMapping
	public String dashboard(Model model) {
		model.addAttribute("stores", storeService.findAll());
		return "admin/dashboard";
	}

	@GetMapping("/stores/new")
	public String newStoreForm(Model model) {
		model.addAttribute("store", new StoreForm());
		return "admin/store-form";
	}

	@PostMapping("/stores/new")
	public String createStore(@ModelAttribute StoreForm storeForm) {
		storeService.createStore(storeForm);
		return "redirect:/admin";
	}

	@PostMapping("/stores/{id}/delete")
	public String deleteStore(@PathVariable("id") Long id) {
		storeService.deleteStore(id);
		return "redirect:/admin";
	}

	@GetMapping("/stores/{storeId}/items/new")
	public String newItemForm(@PathVariable("storeId") Long storeId, Model model) {
		model.addAttribute("item", new ItemForm());
		model.addAttribute("storeId", storeId);
		return "admin/item-form";
	}

	@PostMapping("/stores/{storeId}/items/new")
	public String createItem(@PathVariable("storeId") Long storeId, @ModelAttribute ItemForm itemForm,
			@RequestParam("file") MultipartFile file) throws IOException {
		itemService.addItemToStore(storeId, itemForm, file);
		return "redirect:/admin";
	}
	
	@PostMapping("/items/{itemId}/delete")
	public String deleteItem(@PathVariable("itemId") Long itemId) {
		itemService.deleteItem(itemId);
		return "redirect:/admin";
	}
}