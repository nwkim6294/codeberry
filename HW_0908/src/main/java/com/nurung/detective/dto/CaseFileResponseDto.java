package com.nurung.detective.dto;

import com.nurung.detective.domain.CaseFile;
import com.nurung.detective.domain.CaseStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CaseFileResponseDto {
    private Long id;
    private String caseName;
    private String description;
    private String evidenceImageName;
    private LocalDate reportedDate;
    private CaseStatus status;
    private String detectiveName; // username 노출용

    public static CaseFileResponseDto fromEntity(CaseFile caseFile) {
        return CaseFileResponseDto.builder()
                .id(caseFile.getId())
                .caseName(caseFile.getCaseName())
                .description(caseFile.getDescription())
                .evidenceImageName(caseFile.getEvidenceImageName())
                .reportedDate(caseFile.getReportedDate())
                .status(caseFile.getStatus())
                .detectiveName(caseFile.getDetective().getUsername())
                .build();
    }
}
