package com.bean.batista.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String missionName;
    private String location;
    @Lob
    private String description;
    
    @Enumerated(EnumType.STRING)
    private MissionStatus status;
    
    private String evidencePhotoName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private Agent agent;

    @Builder
    public Mission(String missionName, String location, String description, Agent agent) {
        this.missionName = missionName;
        this.location = location;
        this.description = description;
        this.agent = agent;
        this.status = MissionStatus.ASSIGNED;
    }
    
    public void complete(String evidencePhotoName) {
        if (this.status == MissionStatus.COMPLETED) {
            throw new IllegalStateException("이미 완수된 임무입니다.");
        }
        this.status = MissionStatus.COMPLETED;
        this.evidencePhotoName = evidencePhotoName;
    }
}