package com.sonnh.bookingcar.data.domain;

import com.sonnh.bookingcar.data.domain.embedded.Audit;
import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import com.sonnh.bookingcar.data.domain.enums.ServiceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "service_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, length = 20)
    private String bookingCode;

    @PrePersist
    public void generateBookingCode() {
        if (this.bookingCode == null) {
            this.bookingCode = generateRandomAlphanumeric(6);
        }
    }

    private String generateRandomAlphanumeric(int targetStringLength) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 90; // letter 'Z'
        java.util.Random random = new java.util.Random();
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65)) // loại bỏ các ký tự đặc biệt giữa số và chữ
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tourist_id")
    private User tourist;

    public void addTourist(User tourist) {
        this.tourist = tourist;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    private UUID statusChangedBy;
    private String statusChangeReason;
    private BigDecimal totalPrice;
    private BigDecimal driverAmount;
    private BigDecimal negotiatedPrice;
    private LocalDateTime estimateEndTime;

    @Builder.Default
    private boolean isNegotiated = false;

    @OneToMany(mappedBy = "serviceRequest", fetch = FetchType.LAZY)
    @Builder.Default
    private java.util.List<ServiceSurcharge> surcharges = new java.util.ArrayList<>();

    @OneToOne(mappedBy = "serviceRequest", fetch = FetchType.LAZY)
    private AirportTransferDetail airportTransferDetail;

    @OneToOne(mappedBy = "serviceRequest", fetch = FetchType.LAZY)
    private TourBookingDetail tourBookingDetail;

    @OneToOne(mappedBy = "serviceRequest", fetch = FetchType.LAZY)
    private BookingHistory history;

    @OneToMany(mappedBy = "serviceRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private java.util.List<RequestStatusHistory> statusHistories = new java.util.ArrayList<>();

    @Builder.Default
    @Embedded
    private Audit audit = new Audit();
}
