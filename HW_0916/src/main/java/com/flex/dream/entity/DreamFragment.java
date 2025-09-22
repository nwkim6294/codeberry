package com.flex.dream.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class DreamFragment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dream_project_id") // 외래키
    private DreamProject dreamProject;
    
    // Getters and Setters...
    public String getContent() {
        return content;
    }
    
    
}