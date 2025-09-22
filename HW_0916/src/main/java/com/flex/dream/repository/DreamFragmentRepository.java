package com.flex.dream.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.flex.dream.entity.DreamFragment;

public interface DreamFragmentRepository extends JpaRepository<DreamFragment, Long> {
	@Query(value = "SELECT * FROM dream_fragment WHERE dream_project_id IS NULL", nativeQuery = true)
	List<DreamFragment> findUnassignedFragments();
}
