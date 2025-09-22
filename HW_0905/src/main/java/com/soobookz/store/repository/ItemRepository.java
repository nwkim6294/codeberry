package com.soobookz.store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soobookz.store.domain.Item;
import com.soobookz.store.domain.Store;

public interface ItemRepository extends JpaRepository<Item, Long>{}
