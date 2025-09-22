package com.nurung.detective.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CaseFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String caseName;

    @Lob
    private String description;

    private String evidenceImageName;

    private LocalDate reportedDate;

    @Enumerated(EnumType.STRING) // 이건 enum을 쉽게 쓰는법정도?
    private CaseStatus status;

    @ManyToOne
    private Detective detective;

    @PrePersist
    public void prePersist() {
        this.reportedDate = LocalDate.now();
        this.status = CaseStatus.OPEN;
    }
    
    @Builder
    public CaseFile(String caseName, String description, String evidenceImageName, Detective detective) {
        this.caseName = caseName;
        this.description = description;
        this.evidenceImageName = evidenceImageName;
        this.detective = detective;
    }

    public void update(String caseName, String description) {
        this.caseName = caseName;
        this.description = description;
    }
    
    public void closeCase() {
        this.status = CaseStatus.CLOSED;
    }
}