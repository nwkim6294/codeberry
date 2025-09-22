package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.DiaryEntry;
import com.example.demo.domain.DiaryKeeper;

public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, Long> {
    List<DiaryEntry> findAllByKeeperOrderByCreatedAtDesc(DiaryKeeper keeper);
    Optional<DiaryEntry> findByIdAndKeeper(Long id, DiaryKeeper keeper);
}
