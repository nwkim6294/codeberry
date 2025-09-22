package com.crude.practice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crude.practice.domain.MarketItem;
import com.crude.practice.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {

	private final ItemRepository itemRepository;

	public List<MarketItem> search(String keyword) {
		// TODO Auto-generated method 
		return itemRepository.findByTitleContaining(keyword);
	}

	public List<MarketItem> findAll() {
		// TODO Auto-generated method stub
		return itemRepository.findAll();
	}

	public MarketItem create(MarketItem item) {
		// TODO Auto-generated method stub
		return itemRepository.save(item);
	}

	public MarketItem findById(Long id) {
		// TODO Auto-generated method stub
		return itemRepository.findById(id).orElseThrow();
	}

	public void update(Long id, MarketItem item) {
		// TODO Auto-generated method stub
		MarketItem updateItem = itemRepository.findById(id).orElseThrow();
		updateItem.setTitle(item.getTitle());
		updateItem.setPrice(item.getPrice());
		updateItem.setDescription(item.getDescription());
		
		itemRepository.save(updateItem);
	}

	public void delete(Long id) {
		// TODO Auto-generated method stub
		itemRepository.deleteById(id);
	}
	
	
	
	
	
	
	
	
	
	
	
}
