package com.sonnh.bookingcar.data.domain;

import com.sonnh.bookingcar.data.domain.embedded.Audit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "tours")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String shortDescription;

    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private String imageUrl = "https://media.vov.vn/sites/default/files/styles/large/public/2023-10/phu%20quoc.jpg";

    @Column(columnDefinition = "TEXT")
    private String base64Image;

    private Long duration;

    private LocalTime defaultPickupTime;

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<TourItinerary> itineraries = new ArrayList<>();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VehiclesTypePrice> vehiclePrices = new ArrayList<>();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<TourHighlight> highlights = new ArrayList<>();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SpecialLocationTour> specialLocationMappings = new ArrayList<>();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CheckinTourHistory> checkinTourHistories = new ArrayList<>();

    public void addCheckinHistory(CheckinTourHistory history) {
        this.checkinTourHistories.add(history);
        history.setTour(this);
    }

    public void addItinerary(TourItinerary itinerary) {
        this.itineraries.add(itinerary);
        itinerary.setTour(this);
    }

    public void addHighlight(TourHighlight highlight) {
        this.highlights.add(highlight);
        highlight.setTour(this);
    }

    public void addVehiclePrice(VehiclesTypePrice vehiclePrice) {
        this.vehiclePrices.add(vehiclePrice);
        vehiclePrice.setTour(this);
    }
}
