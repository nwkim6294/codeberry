package com.flex.challenge.service;

import com.flex.challenge.domain.FlexPost;
import com.flex.challenge.domain.User;
import com.flex.challenge.dto.PostDto;
import com.flex.challenge.repository.FlexPostRepository;
import com.flex.challenge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FlexPostService {
    private final FlexPostRepository postRepository;
    private final UserRepository userRepository;
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    private String getFullPath(String filename) { return uploadDir + filename; }
    private String createStoredFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        return uuid + "." + ext;
    }
    
    @Transactional
    public void createPost(PostDto dto, String username) throws IOException {
        User user = userRepository.findByUsername(username).orElseThrow();
        String storedFileName = createStoredFileName(dto.getImageFile().getOriginalFilename());
        dto.getImageFile().transferTo(new File(getFullPath(storedFileName)));

        FlexPost post = FlexPost.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .imageFileName(storedFileName)
                .user(user)
                .build();
        postRepository.save(post);
    }
    
    public Page<FlexPost> findAllPosts(String keyword, Pageable pageable) {
        return postRepository.findByTitleContaining(keyword, pageable);
    }
    
    public FlexPost findById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글입니다."));
    }
    
    public FlexPost findByIdAndValidateOwnership(Long postId, String username) {
        FlexPost post = findById(postId);
        if (!post.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("이 글에 대한 권한이 없습니다.");
        }
        return post;
    }
    
    @Transactional
    public void updatePost(Long postId, PostDto dto, String username) {
        FlexPost post = findByIdAndValidateOwnership(postId, username);
        post.update(dto.getTitle(), dto.getContent());
    }

    @Transactional
    public void deletePost(Long postId, String username) {
        FlexPost post = findByIdAndValidateOwnership(postId, username);
        postRepository.delete(post);
    }
}