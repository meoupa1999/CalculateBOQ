package com.sonnh.bookingcar.data.domain;

import com.sonnh.bookingcar.data.domain.embedded.Audit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "booking_histories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_request_id")
    private ServiceRequest serviceRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    private Shift shift;

    private String driverNameSnapshot;
    private String vehiclePlateSnapshot;
    private String vehicleModelSnapshot;
    
    private LocalTime actualEndTime;
    private String statusSnapshot; // DONE, CANCELLED

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();

    public void addServiceRequest(ServiceRequest serviceRequest) {
        this.setServiceRequest(serviceRequest);
        serviceRequest.setHistory(this);
    }

    public void addShift(Shift shift) {
        this.setShift(shift);
        if (shift.getBookingHistories() == null) {
            shift.setBookingHistories(new ArrayList<>());
        }
        shift.getBookingHistories().add(this);
    }
}
