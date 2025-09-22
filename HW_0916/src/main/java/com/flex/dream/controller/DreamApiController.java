package com.flex.dream.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flex.dream.dto.CodeCheckRequestDto;
import com.flex.dream.dto.CodeCheckResponseDto;
import com.flex.dream.entity.DreamFragment;
import com.flex.dream.entity.DreamProject;
import com.flex.dream.repository.DreamFragmentRepository;
import com.flex.dream.repository.DreamProjectRepository;
import com.flex.dream.service.DreamProjectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class DreamApiController {
	
	private final DreamFragmentRepository fragmentRepository;
	private final DreamProjectRepository projectRepository;
	private final DreamProjectService dreamProjectService;
	
	@PostMapping("/check-code")
	public CodeCheckResponseDto check(@RequestBody CodeCheckRequestDto request) {
		boolean exists = dreamProjectService.checkActivationCodeExists(request.getActivationCode());
		return new CodeCheckResponseDto(exists);
	}
	
	@GetMapping("/unassigned")
	public List<DreamFragment> unassignedFragments() {
		return dreamProjectService.findUnassignedFragments();
		
	}
	
	// 포스트맨 데이터 주입
	@PostMapping
	public DreamProject createProject(@RequestBody DreamProject project) {
	    return projectRepository.save(project);
	}

	@PostMapping("/fragments")
	public DreamFragment createFragment(@RequestBody DreamFragment fragment) {
	    return fragmentRepository.save(fragment);
	}

}
