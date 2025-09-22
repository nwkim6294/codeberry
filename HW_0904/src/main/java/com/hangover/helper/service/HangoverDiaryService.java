package com.hangover.helper.service;

import com.hangover.helper.domain.HangoverDiary;
import com.hangover.helper.domain.Sufferer;
import com.hangover.helper.form.HangoverDiaryForm;
import com.hangover.helper.repository.HangoverDiaryRepository;
import com.hangover.helper.repository.SuffererRepository;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HangoverDiaryService {

    private final HangoverDiaryRepository diaryRepository;
    private final SuffererRepository suffererRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Transactional
    public void saveDiary(HangoverDiaryForm form, MultipartFile file, String username) throws IOException {
    	Sufferer sufferer = suffererRepository.findByUsername(username).orElseThrow();
    	String storedFileName = createStoredFileName(file.getOriginalFilename());
    	file.transferTo(new File(getFullPath(storedFileName)));
    	
        HangoverDiary newDiary = HangoverDiary.builder()
                .title(form.getTitle())
                .symptoms(form.getSymptoms())
                .imageFileName(storedFileName)
                .sufferer(sufferer)
                .build();

        diaryRepository.save(newDiary);
    }
    
    public List<HangoverDiary> findMyDiaries(String username) {
    	Sufferer sufferer = suffererRepository.findByUsername(username).orElseThrow();
    	return diaryRepository.findBySufferer(sufferer);
    }
    
    public HangoverDiary findByIdAndValidateOwnership(Long id, String username) {
        HangoverDiary diary = diaryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일기입니다."));
        if (!diary.getSufferer().getUsername().equals(username)) {
            throw new AccessDeniedException("이 일기에 접근할 권한이 없습니다.");
        }
        return diary;
    }
    
    @Transactional
    public void updateDiary(Long id, HangoverDiaryForm form, String username) {
        HangoverDiary diary = findByIdAndValidateOwnership(id, username);
        diary.update(form.getTitle(), form.getSymptoms());
    }
    
    @Transactional
    public void deleteDiary(Long id, String username) {
    	HangoverDiary diary = findByIdAndValidateOwnership(id, username);
    	diaryRepository.delete(diary);
    }
    
    private String getFullPath(String filename) {return uploadDir + filename;}
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