package com.bean.batista.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgentRole {
    AGENT("ROLE_AGENT"),
    MANAGER("ROLE_MANAGER");

    private final String value;
}