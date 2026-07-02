package com.sonnh.bookingcar.data.domain;

import com.sonnh.bookingcar.data.domain.embedded.Audit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "airport_transfer_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirportTransferDetail {
    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private ServiceRequest serviceRequest;

    private String pickupLocation;
    private String shortPickupLocation;
    private String dropoffLocation;
    private String shortDropoffLocation;
    private String flightNumber;
    private LocalDate pickupDate;
    private LocalTime pickupTime;
    private Integer passengers;
    @Column(name = "vehicle_type")
    private String vehicleTypeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_type_id")
    private VehicleType vehicleType;
    private String notes;
    private String description;
    private String paymentMethod;
    private Double pickupLat;
    private Double pickupLon;
    private Double dropoffLat;
    private Double dropoffLon;

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_shift_id")
    private Shift currentShift;

    public void addServiceRequest(ServiceRequest serviceRequest) {
        serviceRequest.setAirportTransferDetail(this);
        this.setServiceRequest(serviceRequest);
        if (serviceRequest.getId() != null) {
            this.id = serviceRequest.getId();
        }
    }

    public void addShift(Shift shift) {
        this.currentShift = shift;
    }

    public void addVehicleType(VehicleType vehicleType) {
        vehicleType.getAirportTransferDetails().add(this);
        this.setVehicleType(vehicleType);
    }

    public void deleteShift(Shift shift) {
        if (shift != null && shift.getAirportTransferDetails() != null) {
            shift.getAirportTransferDetails().removeIf(detail -> detail.getId().equals(this.id));
        }
        this.currentShift = null;
    }
}
