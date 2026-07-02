package com.sonnh.bookingcar.data.domain;

import com.sonnh.bookingcar.data.domain.embedded.Audit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.sonnh.bookingcar.data.domain.enums.SurchargeType;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "service_surcharges")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceSurcharge {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_request_id", nullable = false)
    private ServiceRequest serviceRequest;

    @Enumerated(EnumType.STRING)
    private SurchargeType type;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();
    
    public void addServiceRequest(ServiceRequest request) {
        this.serviceRequest = request;
        if (request != null && !request.getSurcharges().contains(this)) {
            request.getSurcharges().add(this);
        }
    }
}
