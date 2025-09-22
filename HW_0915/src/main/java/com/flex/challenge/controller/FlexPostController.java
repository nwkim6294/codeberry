package com.flex.challenge.controller;

import com.flex.challenge.domain.FlexPost;
import com.flex.challenge.dto.PostDto;
import com.flex.challenge.service.FlexPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class FlexPostController {

    private final FlexPostService postService;

    @GetMapping
    public String list(Model model, @RequestParam(required = false, defaultValue = "") String keyword,
                       @PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<FlexPost> posts = postService.findAllPosts(keyword, pageable);
        model.addAttribute("posts", posts);
        model.addAttribute("keyword", keyword);
        return "posts/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        FlexPost post = postService.findById(id);
        model.addAttribute("post", post);
        return "posts/detail";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("postDto", new PostDto());
        return "posts/new-form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("postDto") PostDto postDto, BindingResult bindingResult, Principal principal) throws IOException {
        if (bindingResult.hasErrors()) {
            return "posts/new-form";
        }
        postService.createPost(postDto, principal.getName());
        return "redirect:/posts";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Principal principal) {
        try {
            FlexPost post = postService.findByIdAndValidateOwnership(id, principal.getName());
            PostDto dto = new PostDto();
            dto.setTitle(post.getTitle());
            dto.setContent(post.getContent());
            model.addAttribute("postDto", dto);
            model.addAttribute("post", post);
            return "posts/edit-form";
        } catch (AccessDeniedException e) {
            return "redirect:/posts";
        }
    }
    
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("postDto") PostDto postDto, BindingResult bindingResult, Principal principal, Model model) {
        if (bindingResult.hasErrors()) {
             model.addAttribute("post", postService.findById(id)); // To retain post id for delete button
            return "posts/edit-form";
        }
        postService.updatePost(id, postDto, principal.getName());
        return "redirect:/posts";
    }
    
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal) {
        postService.deletePost(id, principal.getName());
        return "redirect:/posts";
    }
}
