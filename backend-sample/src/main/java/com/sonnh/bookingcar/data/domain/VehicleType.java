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
@Table(name = "vehicle_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(name = "base_price", precision = 19, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "price_per_km", precision = 19, scale = 2)
    private BigDecimal pricePerKm;

    @Column(name = "base_km")
    private Double baseKm; // Số km đầu tiên cho giá mở cửa

    @Builder.Default
    @Column(name = "is_distance_booking_enabled")
    private Boolean isDistanceBookingEnabled = true;

    @OneToMany(mappedBy = "vehicleType")
    @Builder.Default
    private List<VehiclesTypePrice> tourPrices = new ArrayList<>();

    @OneToMany(mappedBy = "vehicleType")
    @Builder.Default
    private List<Vehicle> vehicles = new ArrayList<>();

    @OneToMany(mappedBy = "vehicleType")
    @Builder.Default
    private List<AirportTransferDetail> airportTransferDetails = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();

}
