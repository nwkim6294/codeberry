package com.bean.batista.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MissionRequestDto {
    private String missionName;
    private String location;
    private String description;
    private Long agentId;
}
