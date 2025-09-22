package com.nurung.detective.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.nurung.detective.domain.CaseFile;
import com.nurung.detective.domain.CaseStatus;
import com.nurung.detective.domain.Detective;
import com.nurung.detective.dto.CaseFileRequestDto;
import com.nurung.detective.dto.CaseFileResponseDto;
import com.nurung.detective.repository.CaseFileRepository;
import com.nurung.detective.repository.DetectiveRepository;

import jakarta.persistence.criteria.CriteriaBuilder.Case;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaseFileService {
	
	private final CaseFileRepository caseFileRepository;
	private final DetectiveRepository detectiveRepository;
	
    @Value("${file.upload-dir}")
    private String uploadDir;
    
	public void createCase(CaseFileRequestDto dto, MultipartFile file, String username) throws IOException {
		Detective detective = detectiveRepository.findByUsername(username).orElseThrow();
		
		String storedFileName = null;
		if(file != null && !file.isEmpty()) {
			storedFileName = createStoredFileName(file.getOriginalFilename());
			file.transferTo(new File(getFullPath(storedFileName)));
		}
		
		CaseFile newCase = CaseFile.builder()
				.caseName(dto.getCaseName())
				.description(dto.getDescription())
				.evidenceImageName(storedFileName)
				.detective(detective)
				.build();
		
		caseFileRepository.save(newCase);
	}    
	
    

	public List<CaseFile> findMyCases(String username) {
		Detective detective = detectiveRepository.findByUsername(username).orElseThrow();
		return caseFileRepository.findByDetective(detective);
	}

	public List<CaseFile> findClosedCases() {
		return caseFileRepository.findByStatus(CaseStatus.CLOSED);
	}

	public CaseFile findByIdAndValidateOwnership(Long id, String username) {
		CaseFile caseFile = caseFileRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사건 파일입니다"));
		if(!caseFile.getDetective().getUsername().equals(username)) {
			throw new AccessDeniedException("이 사건 파일에 접근할 권한이 없습니다.");
		}
		return caseFile;
	}
	
	@Transactional
	public void updateCase(Long id, CaseFileRequestDto dto, String username) {
		CaseFile caseFile = findByIdAndValidateOwnership(id, username);
		if(caseFile.getStatus() == CaseStatus.CLOSED) {
			throw new IllegalStateException("종결된 사건은 수정할 수 없습니다.");
		}
		caseFile.update(dto.getCaseName(), dto.getDescription());
	}
	

	@Transactional
	public void closeCase(Long id, String username) {
		CaseFile casFile = findByIdAndValidateOwnership(id, username);
		casFile.closeCase();
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

