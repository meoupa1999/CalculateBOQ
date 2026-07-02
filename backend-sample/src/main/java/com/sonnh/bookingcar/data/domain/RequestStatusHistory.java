package com.sonnh.bookingcar.data.domain;

import com.sonnh.bookingcar.data.domain.embedded.Audit;
import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "request_status_histories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User actionBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_request_id")
    private ServiceRequest serviceRequest;

    private String role;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(name = "reason_note")
    private String reasonNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatched_driver_id")
    private User dispatchedDriver;

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();

    public void addActionBy(User user) {
        this.setActionBy(user);
    }

    public void addServiceRequest(ServiceRequest serviceRequest) {
        this.setServiceRequest(serviceRequest);
    }

    public void addDispatchedDriver(User user) {
        this.setDispatchedDriver(user);
    }
}
