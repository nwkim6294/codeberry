package com.flex.dream.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.flex.dream.entity.DreamFragment;
import com.flex.dream.repository.DreamFragmentRepository;
import com.flex.dream.repository.DreamProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DreamProjectService {
	private final DreamFragmentRepository dreamFragmentRepository;
	private final DreamProjectRepository dreamProjectRepository;
	
	public boolean checkActivationCodeExists(String code) {
		return dreamProjectRepository.existsByActivationCode(code);
	}

	public List<DreamFragment> findUnassignedFragments() {
		return dreamFragmentRepository.findUnassignedFragments();
	}

}
