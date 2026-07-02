package com.sonnh.bookingcar.data.domain;

import com.sonnh.bookingcar.data.domain.embedded.Audit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "car_rental_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarRentalDetail {
    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private ServiceRequest serviceRequest;

    private LocalDate startDate;
    private LocalDate endDate;
    @Builder.Default
    private Boolean withDriver = false;

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();
}
