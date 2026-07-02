package com.sonnh.bookingcar.data.domain;

import com.sonnh.bookingcar.data.domain.embedded.Audit;
import com.sonnh.bookingcar.data.domain.enums.CheckinStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "checkin_tour_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckinTourHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private CheckinStatus status;

    @Column(columnDefinition = "TEXT")
    private String reasonNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    private Tour tour;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "special_location_tour_id")
    private SpecialLocationTour specialLocationTour;

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();

    public void addUser(User user) {
        user.getCheckinTourHistories().add(this);
        this.setUser(user);
    }

    public void addTour(Tour tour) {
        tour.getCheckinTourHistories().add(this);
        this.setTour(tour);
    }

    public void addSpecialLocationTour(SpecialLocationTour mapping) {
        mapping.setCheckinHistory(this);
        this.setSpecialLocationTour(mapping);
    }
}
