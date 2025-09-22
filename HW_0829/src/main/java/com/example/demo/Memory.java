package com.example.demo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Memory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String source; // 기억의 출처

    private String taste; // 맛에 대한 묘사

    private int rating; // 별점 (1~5)
    
    protected Memory() {}
    
    public void update(Memory other) {
        this.source = other.getSource();
        this.taste = other.getTaste();
        this.rating = other.getRating();
    }
}
