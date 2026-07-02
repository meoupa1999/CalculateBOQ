package com.sonnh.bookingcar.data.domain;

import com.sonnh.bookingcar.data.domain.embedded.Audit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "vehicle_incidents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleIncident {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_history_id")
    private ShiftHistory shiftHistory;

    private String type; // DISCREPANCY, ACCIDENT, MECHANICAL, CLEANING
    private String description;
    
    @Builder.Default
    private String severity = "LOW"; // LOW, MEDIUM, HIGH
    
    @Builder.Default
    private Boolean isResolved = false;

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();
}
