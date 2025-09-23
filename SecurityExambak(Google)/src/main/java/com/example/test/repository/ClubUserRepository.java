package com.example.test.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.test.domain.ClubUser;
import com.example.test.domain.ClubUser.ClubRole;

@Repository
public interface ClubUserRepository extends JpaRepository<ClubUser, Long> {
    Optional<ClubUser> findByUsername(String username);
    
    Optional<ClubUser> findByEmail(String email);
    
    Optional<ClubUser> findBySocialId(String socialId);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    // 사용자 통계를 위한 메서드들
    long countByEnabledTrue();
     
    long countByRolesContaining(ClubRole role);
}
