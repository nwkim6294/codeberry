package com.soobookz.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soobookz.store.domain.Store;

public interface StoreRepository extends JpaRepository<Store, Long>{
	
}
