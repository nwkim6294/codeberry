package com.flex.dream.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class DreamProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;

    @Column(unique = true, nullable = false)
    private String activationCode;

    @OneToMany(mappedBy = "dreamProject", cascade = CascadeType.ALL)
    private List<DreamFragment> fragments = new ArrayList<>();

    public DreamProject(String projectName, String activationCode) {
        this.projectName = projectName;
        this.activationCode = activationCode;
    }
}