package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemoryService {
	
	private final MemoryRepository memoryRepository;

	//리스트 불러오기
    public List<Memory> findAll() {
        return memoryRepository.findAll();
    }

	public Memory save(Memory memory) {
		return memoryRepository.save(memory);
	}

	public Memory findById(Long id) {
	    return this.memoryRepository.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("해당 ID의 기억이 없습니다: " + id));
	}
	
	@Transactional
	public void update(Long id, Memory memory) {
	    Memory memory1 = findById(id);
	    memory1.update(memory);
	}
	
	@Transactional
	public void delete(Long id) {
		Memory memory = findById(id);
		memoryRepository.delete(memory);
	}
	
}
