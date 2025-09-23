package com.bean.batista.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.AccessDeniedException;

import com.bean.batista.domain.Agent;
import com.bean.batista.domain.Mission;
import com.bean.batista.dto.MissionDto;
import com.bean.batista.repository.AgentRepository;
import com.bean.batista.repository.MissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionService {
	
	@Value("${file.upload-dir}")
	private String uploadDir;
	
	
	private final MissionRepository missionRepository;
	private final AgentRepository agentRepository;
	

    
    @Transactional
    public void createMission(MissionDto dto, Long agentId) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Mission newMission = Mission.builder()
                .missionName(dto.getMissionName())
                .location(dto.getLocation())
                .description(dto.getDescription())
                .agent(agent)
                .build();

        missionRepository.save(newMission);
    }

    
    public List<Mission> findAllMissions() {
        return missionRepository.findAll();
    }	
    
    public List<Mission> findMyMissions(String username) {
        Agent agent = agentRepository.findByUsername(username).orElseThrow();
        return missionRepository.findByAgent(agent);
    }

	public Mission findByIdAndValidateOwnerShip(Long missionId, String username) {
		Mission mission = missionRepository.findById(missionId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 임무입니다."));
		if (!mission.getAgent().getUsername().equals(username)) {
			throw new AccessDeniedException("이 임무에 대한 접근 권한이 없습니다.");
		}
		
		return mission;
	}

	@Transactional
	public void completeMission(Long missionId, MultipartFile file, String username) {
	    Mission mission = findByIdAndValidateOwnerShip(missionId, username);

	    // 저장할 파일명 생성
	    String storedFileName = createStoredFileName(file.getOriginalFilename());

	    // 풀 경로
	    String fullPath = getFullPath(storedFileName);

	    try {
	        // 실제 파일 저장
	        file.transferTo(new File(fullPath));
	    } catch (IOException e) {
	        throw new RuntimeException("파일 저장 실패: " + e.getMessage(), e);
	    }

	    // DB 업데이트
	    mission.complete(storedFileName);
	    missionRepository.save(mission); // mission이 영속 상태면 save 생략 가능
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
