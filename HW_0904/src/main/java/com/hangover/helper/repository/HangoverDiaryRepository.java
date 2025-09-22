package com.hangover.helper.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hangover.helper.domain.HangoverDiary;
import com.hangover.helper.domain.Sufferer;

public interface HangoverDiaryRepository extends JpaRepository<HangoverDiary, Long> {
    List<HangoverDiary> findBySufferer(Sufferer sufferer);
}
