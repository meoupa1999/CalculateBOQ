package com.sonnh.bookingcar.data.domain;

import com.sonnh.bookingcar.data.domain.embedded.Audit;
import com.sonnh.bookingcar.data.domain.enums.ShiftStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "shifts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ShiftStatus status = ShiftStatus.CLOSED;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();

    @OneToMany(mappedBy = "shift", fetch = FetchType.LAZY)
    @Builder.Default
    private List<ShiftHistory> shiftHistories = new ArrayList<>();

    @OneToMany(mappedBy = "currentShift", fetch = FetchType.LAZY)
    @Builder.Default
    private List<AirportTransferDetail> airportTransferDetails = new ArrayList<>();

    @OneToMany(mappedBy = "currentShift", fetch = FetchType.LAZY)
    @Builder.Default
    private List<TourBookingDetail> tourBookingDetails = new ArrayList<>();

    @OneToMany(mappedBy = "shift", fetch = FetchType.LAZY)
    @Builder.Default
    private List<BookingHistory> bookingHistories = new ArrayList<>();

    public void addDriver(User driver) {
        driver.getShiftList().add(this);
        this.setDriver(driver);
    }

    public void addVehicle(Vehicle vehicle) {
        vehicle.getShiftList().add(this);
        this.setVehicle(vehicle);
    }
}
