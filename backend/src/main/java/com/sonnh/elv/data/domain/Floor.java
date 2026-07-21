package com.sonnh.elv.data.domain;

import com.sonnh.elv.data.domain.embedded.Audit;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.UUID;

@Entity
@Table(
    name = "floor",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tower_id", "floorIndex"})
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Floor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(nullable = false)
    private Integer floorIndex;

    private String floorName;

    private Integer cameraCount;

    private Integer domeCount;

    private Integer bulletCount;

    private Integer cabinetCount;

    private Integer sw24Count;

    private Integer sw16Count;

    private String upsType;

    private Integer pduCount;

    private Integer converterCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tower_id")
    private Tower tower;

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();
}
