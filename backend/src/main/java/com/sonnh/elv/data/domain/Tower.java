package com.sonnh.elv.data.domain;

import com.sonnh.elv.data.domain.embedded.Audit;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tower")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tower {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(nullable = false)
    private String name;

    private Integer floorCount;
    private Integer basementCount;
    private Boolean hasRoof;
    private Double widthLength;
    private Double heightLength;
    private Integer quantity2U;

    @Column(columnDefinition = "TEXT")
    private String specialName; // JSON mapping of floorIndex -> specialName

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "config_id")
    private Config config;

    @Builder.Default
    @OneToMany(mappedBy = "tower")
    private List<Floor> floors = new ArrayList<>();

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();

    public void addProject(Project project) {
        project.getTowers().add(this);
        this.setProject(project);
    }

    public void addConfig(Config config) {
        config.getTowers().add(this);
        this.setConfig(config);
    }
}
