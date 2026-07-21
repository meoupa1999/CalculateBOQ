package com.sonnh.elv.data.domain;

import com.sonnh.elv.data.domain.embedded.Audit;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "floor", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "tower_id", "floorIndex" })
})
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

    private Integer fromIndex;

    private Integer toIndex;

    private Integer cabinetIndex;

    private Boolean isCabinetPlaced;

    private Integer cableLength;

    private Integer atrium;

    private Integer downCabinet;

    private Integer inCabinet;

    private Integer autocadLength;

    @Builder.Default
    @OneToMany(mappedBy = "floor")
    private List<Cabinet> cabinets = new java.util.ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tower_id")
    private Tower tower;

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();

    public void addTower(Tower tower) {
        tower.getFloors().add(this);
        this.setTower(tower);
    }
}
