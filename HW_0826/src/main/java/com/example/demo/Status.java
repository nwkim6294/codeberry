package com.example.demo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 배송 상태를 관리하는 Enum
@Getter
@RequiredArgsConstructor
public enum Status {
    PENDING("접수 대기"),
    IN_TRANSIT("배송 중"),
    DELIVERED("배달 완료");

    private final String description;
}