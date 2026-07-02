package com.sonnh.elv.data.domain;

import com.sonnh.elv.data.domain.embedded.Audit;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Entity
@Table(name = "project")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "project")
    private List<Tower> towers = new ArrayList<>();

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();
}
