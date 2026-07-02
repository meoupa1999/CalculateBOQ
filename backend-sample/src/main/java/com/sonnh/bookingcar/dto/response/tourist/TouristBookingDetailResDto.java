package com.sonnh.bookingcar.dto.response.tourist;

import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import com.sonnh.bookingcar.data.domain.enums.ServiceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TouristBookingDetailResDto {
    private UUID id;
    private String bookingCode;
    private ServiceType type;
    private BookingStatus status;
    private String pickupLocation;
    private String dropoffLocation;
    private String pickupDate;
    private String pickupTime;
    private Integer passengers;
    private String flightNumber;
    private String notes;
    private String vehicleTypeRequested;
    private String vehicleCategory;
    private BigDecimal tourPrice;
    private BigDecimal totalPrice;
    private String paymentMethod;
    private String driverName;
    private String vehicleInfo;
    private String createdAt;
    private java.time.LocalDateTime estimatedEndDate;

    private CancelReasonResDto cancelReason;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancelReasonResDto {
        private String reasonNote;
        private String role;
        private String userName;
    }
}
