package com.example.demo.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.domain.DiaryEntry;
import com.example.demo.domain.DiaryKeeper;
import com.example.demo.repository.DiaryEntryRepository;
import com.example.demo.repository.DiaryKeeperRepository;


@Controller
@RequestMapping("/entries")
public class DiaryEntryController {
	
	private final DiaryEntryRepository entryRepository;
	private final DiaryKeeperRepository keeperRepository;
	
	public DiaryEntryController(DiaryEntryRepository entryRepository, DiaryKeeperRepository keeperRepository) {
		this.entryRepository = entryRepository;
		this.keeperRepository = keeperRepository;
	}

	private DiaryKeeper getCurrentKeeper(Principal principal) {
        // 람다식으로 표현하는 방법
        // return keeperRepository.findByUsername(principal.getName())
        //        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
		Optional<DiaryKeeper> keeperOptional = keeperRepository.findByUsername(principal.getName());
		if(!keeperOptional.isPresent()) {
			throw new UsernameNotFoundException("사용자를 찾을 수 없습니다");
		}
		
		return keeperOptional.get();
	}
	
	@GetMapping
	public String getMyEntries(Model model, Principal principal) {
		DiaryKeeper currentKeeper = getCurrentKeeper(principal);
		List<DiaryEntry> entries = entryRepository.findAllByKeeperOrderByCreatedAtDesc(currentKeeper);
		model.addAttribute("entries", entries);
		return "entries/list";
	}
	
	@GetMapping("/new")
	public String showNewEntryForm(Model model) {
		model.addAttribute("entry", new DiaryEntry());
		return "entries/new-form";
	}
	
	@PostMapping("/new")
	public String createEntry(@ModelAttribute DiaryEntry entry, Principal principal) {
		DiaryKeeper currentKeeper = getCurrentKeeper(principal);
		entry.setKeeper(currentKeeper);
		entryRepository.save(entry);
		return "redirect:/entries";
	}
	
	@GetMapping("/{id}/edit")
	public String showEditEntryForm(@PathVariable("id") Long id, Model model, Principal principal) {
		DiaryKeeper currentKeeper = getCurrentKeeper(principal);
		
        // 람다식으로 표현하는 방법
        // DiaryEntry entry = entryRepository.findByIdAndKeeper(id, currentKeeper)
        //        .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "이 일기를 수정할 권한이 없습니다."))
		
		Optional<DiaryEntry> entryOptional = entryRepository.findByIdAndKeeper(id, currentKeeper);
		if(!entryOptional.isPresent()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이 일기를 수정할 권한이 없습니다.");
		}
		
		model.addAttribute("entry", entryOptional.get());
		return "entries/edit-form";
	}
	
	@PostMapping("/{id}/edit")
	public String updateEntry(@PathVariable("id") Long id, @ModelAttribute DiaryEntry updatedEntry, Principal principal) {
		DiaryKeeper currentKeeper = getCurrentKeeper(principal);
		
		Optional<DiaryEntry> entryOptional = entryRepository.findByIdAndKeeper(id, currentKeeper);
		if(!entryOptional.isPresent()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이 일기를 수정할 권한이 없습니다.");
		}
		
		DiaryEntry entry = entryOptional.get();
		entry.setTitle(updatedEntry.getTitle());
		entry.setContent(updatedEntry.getContent());
		entryRepository.save(entry);
		
		return "redirect:/entries";
	}
	
	@PostMapping("/{id}/delete")
	public String deleteEntry(@PathVariable("id") Long id, Principal principal) {
        DiaryKeeper currentKeeper = getCurrentKeeper(principal);

        Optional<DiaryEntry> entryOptional = entryRepository.findByIdAndKeeper(id, currentKeeper);
        if (!entryOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이 일기를 삭제할 권한이 없습니다.");
        }
        
        entryRepository.delete(entryOptional.get());
        return "redirect:/entries";
	}
	
}
