package com.sonnh.bookingcar.data.domain;

import com.sonnh.bookingcar.data.domain.embedded.Audit;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "special_locations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String locationName;
    private Double latitude;
    private Double longitude;

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();

    @OneToMany(mappedBy = "specialLocation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<SpecialLocationTour> tourMappings = new ArrayList<>();
}
