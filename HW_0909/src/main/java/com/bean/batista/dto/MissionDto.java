package com.bean.batista.dto;

import com.bean.batista.domain.MissionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissionDto {
    private Long id;
    private String missionName;
    private String location;
    private String description;
    private MissionStatus status;
    private String evidencePhotoName;
    private String agentName;
}
