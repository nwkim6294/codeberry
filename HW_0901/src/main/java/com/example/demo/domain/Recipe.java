package com.example.demo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Recipe {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Lob
    private String description;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)    private LabUser labUser;

    
    @Builder
    public Recipe(String name, String description, LabUser labUser) {
        this.name = name;
        this.description = description;
        this.labUser = labUser;
    }
    
    public Recipe() {}
    
    public void update(String name, String description) {
        this.name = name;
        this.description = description;
    }
}