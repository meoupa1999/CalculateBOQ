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
@Table(name = "tour_booking_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourBookingDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private ServiceRequest serviceRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    private Tour tour;

    public void addTour(Tour tour) {
        this.tour = tour;
    }

    private String pickupLocation;
    private LocalDate pickupDate;
    private LocalTime actualPickupTime;
    private Integer numberOfPeople;
    private String notes;
    private String paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_shift_id")
    private Shift currentShift;

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_type_price_id")
    private VehiclesTypePrice vehicleTypePrice;

    public void addVehicleTypePrice(VehiclesTypePrice vehicleTypePrice) {
        vehicleTypePrice.getBookings().add(this);
        this.setVehicleTypePrice(vehicleTypePrice);
    }

    public void addServiceRequest(ServiceRequest serviceRequest) {
        this.serviceRequest = serviceRequest;
        serviceRequest.setTourBookingDetail(this);
    }

    public void addShift(Shift shift) {
        this.currentShift = shift;
    }

    public void deleteShift(Shift shift) {
        if (shift != null && shift.getTourBookingDetails() != null) {
            shift.getTourBookingDetails().removeIf(detail -> detail.getId().equals(this.id));
        }
        this.currentShift = null;
    }
}
