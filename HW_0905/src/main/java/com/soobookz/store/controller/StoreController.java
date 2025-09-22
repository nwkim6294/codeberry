package com.soobookz.store.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soobookz.store.service.StoreService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {
	
    private final StoreService storeService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("stores", storeService.findAll());
        return "stores/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("store", storeService.findById(id));
        return "stores/detail";
    }
}
