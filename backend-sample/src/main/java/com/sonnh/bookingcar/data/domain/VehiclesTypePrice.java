package com.sonnh.bookingcar.data.domain;

import com.sonnh.bookingcar.data.domain.embedded.Audit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "tour_vehicle_type_prices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiclesTypePrice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_type_id")
    private VehicleType vehicleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = true)
    private Tour tour;

    @OneToMany(mappedBy = "vehicleTypePrice")
    @Builder.Default
    private List<TourBookingDetail> bookings = new ArrayList<>();

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();

    public void addTour(Tour tour) {
        tour.getVehiclePrices().add(this);
        this.setTour(tour);
    }

    public void addVehicleType(VehicleType vehicleType) {
        vehicleType.getTourPrices().add(this);
        this.setVehicleType(vehicleType);
    }
}
