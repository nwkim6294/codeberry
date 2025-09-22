package com.example.demo;

import jakarta.persistence.*;

@Entity
public class Anomaly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 변칙 현상 이름

    private String location; // 발생 위치

    private String riskLevel; // 위험 등급 (예: 낮음, 중간, 높음, 재앙)

    // JPA를 위한 기본 생성자
    protected Anomaly() {}

    // 객체 생성을 위한 생성자
    public Anomaly(String name, String location, String riskLevel) {
        this.name = name;
        this.location = location;
        this.riskLevel = riskLevel;
    }

    // 수정 기능을 위한 메서드
    public void update(String name, String location, String riskLevel) {
        this.name = name;
        this.location = location;
        this.riskLevel = riskLevel;
    }

    // Getter
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getRiskLevel() { return riskLevel; }
    
    // View에서 form 데이터를 바인딩하기 위해 Setter가 필요합니다.
    public void setName(String name) { this.name = name; }
    public void setLocation(String location) { this.location = location; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
}