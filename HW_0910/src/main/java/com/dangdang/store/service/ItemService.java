package com.dangdang.store.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangdang.store.domain.MarketItem;
import com.dangdang.store.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {
	private final ItemRepository itemRepository;
	
	public List<MarketItem> search(String keyword) {
        return itemRepository.findByTitleContaining(keyword);
	}

	public List<MarketItem> findAll() {
		return itemRepository.findAll();
	}

	public MarketItem create(MarketItem item) {
		return itemRepository.save(item);
	}

	public MarketItem findById(Long id) {
		return itemRepository.findById(id).orElseThrow();
	}

	public void update(Long id, MarketItem item) {
	    MarketItem updateItem = itemRepository.findById(id).orElseThrow();
	    updateItem.setTitle(item.getTitle());
	    updateItem.setPrice(item.getPrice());
	    updateItem.setDescription(item.getDescription());
	   
	    itemRepository.save(updateItem);
	}

	public void delete(Long id) {
	    itemRepository.deleteById(id);
	}

}
