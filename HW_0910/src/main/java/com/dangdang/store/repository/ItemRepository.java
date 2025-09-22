package com.dangdang.store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dangdang.store.domain.MarketItem;


public interface ItemRepository extends JpaRepository<MarketItem, Long>{
	
	@Query("SELECT i FROM Item i WHERE i.title LIKE %:keyword%")
	List<MarketItem> findByTitleContaining(@Param("keyword") String keyword);
}
