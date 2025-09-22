package com.hangover.helper.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HangoverDiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    private String symptoms;

    private String imageFileName;

    private LocalDate sufferedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sufferer_id")
    private Sufferer sufferer;

    @PrePersist //날짜 새롭게 들어올 때마다 자동으로 찍는 어노테이션
    public void prePersist() {
        this.sufferedDate = LocalDate.now();
    }
    
    @Builder
    public HangoverDiary(String title, String symptoms, String imageFileName, Sufferer sufferer) {
        this.title = title;
        this.symptoms = symptoms;
        this.imageFileName = imageFileName;
        this.sufferer = sufferer;
    }

    public void update(String title, String symptoms) {
        this.title = title;
        this.symptoms = symptoms;
    }
}