package com.nurung.detective.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nurung.detective.domain.CaseFile;
import com.nurung.detective.domain.CaseStatus;
import com.nurung.detective.domain.Detective;

public interface CaseFileRepository extends JpaRepository<CaseFile, Long>{
	// 특정 탐지견(detective)의 모든 사건 파일을 찾는 메서드
	List<CaseFile> findByDetective(Detective detective);
	// 특정 상태(status)의 모든 사건 파일을 찾는 메서드
	List<CaseFile> findByStatus(CaseStatus status);

}
