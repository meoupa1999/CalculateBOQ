package com.sonnh.bookingcar.data.domain;

import com.sonnh.bookingcar.data.domain.embedded.Audit;

import com.sonnh.bookingcar.data.domain.enums.VehicleStatus;
import com.sonnh.bookingcar.data.domain.enums.VehicleOwnership;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "vehicles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String model;

    @Column(unique = true, nullable = false)
    private String plateNumber;

    @Column
    private String type; // 4_SEATS, 7_SEATS, 16_SEATS

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_type_id")
    private VehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    private VehicleStatus status; // AVAILABLE, MAINTENANCE, BUSY

    private String year;
    private String color;

    @Enumerated(EnumType.STRING)
    private VehicleOwnership ownershipType;

    private java.math.BigDecimal currentMileage;
    private Integer currentFuelLevel;

    // Fleet Health Tracking
    private java.math.BigDecimal lastMaintenanceMileage;
    private java.math.BigDecimal maintenanceInterval; // e.g., 5000
    private java.math.BigDecimal maintenanceDueMileage; // lastMaintenanceMileage + maintenanceInterval

    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Shift> shiftList = new ArrayList<>();

    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Document> documents = new ArrayList<>();

    @OneToMany(mappedBy = "vehicle")
    @Builder.Default
    private List<Image> images = new ArrayList<>();

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();

    public void addVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
        if (vehicleType != null) {
            this.type = vehicleType.getName();
            if (vehicleType.getVehicles() == null) {
                vehicleType.setVehicles(new java.util.ArrayList<>());
            }
            vehicleType.getVehicles().add(this);
        }
    }
}
