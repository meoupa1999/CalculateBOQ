package com.sonnh.bookingcar.data.domain;

import com.sonnh.bookingcar.data.domain.embedded.Audit;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "tour_special_locations_mapping")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialLocationTour {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    private Tour tour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "special_location_id")
    private SpecialLocation specialLocation;

    @OneToOne(mappedBy = "specialLocationTour", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CheckinTourHistory checkinHistory;

    private Integer priority;

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();

    public void addTour(Tour tour) {
       tour.getSpecialLocationMappings().add(this);
       this.setTour(tour);
    }

    public void addSpecialLocation(SpecialLocation specialLocation) {
       specialLocation.getTourMappings().add(this);
       this.setSpecialLocation(specialLocation);
    }

    public void setCheckinHistory(CheckinTourHistory history) {
        this.checkinHistory = history;
        history.setSpecialLocationTour(this);
    }
}
