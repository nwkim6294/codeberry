package com.soobookz.store.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.soobookz.store.domain.Item;
import com.soobookz.store.domain.Store;
import com.soobookz.store.form.ItemForm;
import com.soobookz.store.repository.ItemRepository;
import com.soobookz.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
	private final ItemRepository itemRepository;
	private final StoreRepository storeRepository;
	
	@Value("${file.upload-dir}")
	private String uploadDir;

	@Transactional
	public void addItemToStore(Long storeId, ItemForm itemForm, MultipartFile file) throws IOException {
		Store store = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지점입니다."));
		String storedFileName = createStoredFileName(file.getOriginalFilename());
		file.transferTo(new File(getFullPath(storedFileName)));
		
		Item newItem = Item.builder()
				.name(itemForm.getName())
				.price(itemForm.getPrice())
				.stock(itemForm.getStock())
				.imageFileName(storedFileName)
				.store(store)
				.build();
		itemRepository.save(newItem);
	}
	
	@Transactional
	public void deleteItem(Long itemId) {
		itemRepository.deleteById(itemId);
	}
	
	private String getFullPath(String filename) {
		return uploadDir + filename;
	}
	
	private String createStoredFileName(String originalFilename) {
		String uuid = UUID.randomUUID().toString();
		String ext = extractExt(originalFilename);
		return uuid + "." + ext;
	}
	
	private String extractExt(String originalFilename) {
		int pos = originalFilename.lastIndexOf(".");
		return originalFilename.substring(pos + 1);
	}
	
}
