package com.example.demo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sender;

    private String receiver;
    private String destination;
    private String content;

    @Enumerated(EnumType.STRING) // Enum 타입을 문자열로 DB에 저장
    private Status status;

    @Builder
    public Parcel(String sender, String receiver, String destination, String content, Status status) {
        this.sender = sender;
        this.receiver = receiver;
        this.destination = destination;
        this.content = content;
        this.status = status;
    }

    // 상태 변경을 위한 메서드
    public void updateStatus(Status status) {
        this.status = status;
    }
}