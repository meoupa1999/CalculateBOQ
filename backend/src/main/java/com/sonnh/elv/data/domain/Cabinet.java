package com.sonnh.elv.data.domain;

import com.sonnh.elv.data.domain.embedded.Audit;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.UUID;

@Entity
@Table(name = "cabinet")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cabinet {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    private String cabinetType;

    private Integer cameraQuantity;

    private Integer sw24Count;

    private Integer sw16Count;

    private Integer upsCount;

    private Integer pduCount;

    private Integer converterCount;

    @Column(columnDefinition = "TEXT")
    private String allocationsJson;

    @ManyToOne
    @JoinColumn(name = "floor_id")
    private Floor floor;

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    public void addFloor(Floor floor) {
        floor.getCabinets().add(this);
        this.setFloor(floor);
    }
}
