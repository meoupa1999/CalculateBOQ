package com.sonnh.bookingcar.dto.response.driver;

import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import com.sonnh.bookingcar.data.domain.enums.ServiceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverAirportDetailResDto {
    private UUID requestId;
    private String bookingCode;
    private ServiceType type;
    private BookingStatus status;
    private String customerName;
    private String customerPhone;
    private String notes;
    private String paymentMethod;
    private Double totalPrice;
    private java.time.LocalDateTime estimateEndTime;
    private AirportDetailData airportDetail;

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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AirportDetailData {
        private String pickupLocation;
        private String dropoffLocation;
        private Double pickupLat;
        private Double pickupLon;
        private Double dropoffLat;
        private Double dropoffLon;
        private LocalDate pickupDate;
        private LocalTime pickupTime;
        private String flightNumber;
        private Integer passengers;
        private String vehicleType;
    }
}
