package com.nurung.detective.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CaseFileRequestDto {
    private String caseName;
    private String description;
    // 파일은 MultipartFile로 따로 받음 → DTO에 포함하지 않음
}
