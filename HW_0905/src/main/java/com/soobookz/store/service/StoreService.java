package com.soobookz.store.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soobookz.store.domain.Store;
import com.soobookz.store.form.StoreForm;
import com.soobookz.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {
	private final StoreRepository storeRepository;
	
	public List<Store> findAll() {
		return storeRepository.findAll();		
	}
	
	public Store findById(Long storeId) {
		return storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지점입니다."));
	}

	@Transactional
	public void createStore(StoreForm storeForm) {
		Store store = Store.builder()
				.name(storeForm.getName())
				.location(storeForm.getLocation())
				.build();
		storeRepository.save(store);
	}
	
	@Transactional
	public void deleteStore(Long storeId) {
		storeRepository.deleteById(storeId);
	}
}
