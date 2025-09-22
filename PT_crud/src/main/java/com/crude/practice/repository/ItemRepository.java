package com.crude.practice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crude.practice.domain.MarketItem;

public interface ItemRepository extends JpaRepository<MarketItem, Long> {
	List<MarketItem> findByTitleContaining(String keword);
}
