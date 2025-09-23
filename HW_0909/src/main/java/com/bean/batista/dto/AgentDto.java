package com.bean.batista.dto;

import com.bean.batista.domain.AgentRole;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgentDto {
    private String username;
    private String password;
    private AgentRole role;

}
