package com.flex.challenge.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PostDto {
    @NotEmpty(message = "제목은 필수 항목입니다.")
    private String title;

    @NotEmpty(message = "내용은 필수 항목입니다.")
    @Size(min = 10, message = "내용은 최소 10자 이상 입력해야 합니다.")
    private String content;

    private MultipartFile imageFile;
}
