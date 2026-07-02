package com.sonnh.bookingcar.dto.response.admin;

import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import com.sonnh.bookingcar.data.domain.enums.ServiceType;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAirportBookingResDto {
    private UUID id;
    private String bookingCode;
    private ServiceType type;
    private String pickupLocation;
    private String dropoffLocation;
    private String date;
    private String time;
    private Integer passengers;
    private String flightNumber;
    private String notes;
    private String description;
    private String paymentMethod;
    private BigDecimal totalPrice;
    private Double pickupLat;
    private Double pickupLon;
    private Double dropoffLat;
    private Double dropoffLon;
    private BookingStatus status;
    private String createdAt;
    private String driverName;
    private String vehicleInfo;
    private TouristDto tourist;
    private LocalDateTime estimatedEndDate;
    private VehicleTypeResDto vehicleType;

    @JsonProperty("currentShift")
    private ShiftDto currentShift;

    private CancelReasonResDto cancelReason;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleTypeResDto {
        private UUID id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TouristDto {
        private UUID id;
        private String name;
        private String email;
        private String phone;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShiftDto {
        private UUID id;
        private DriverDto driver;
        private VehicleDto vehicle;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DriverDto {
        private UUID id;
        private String name;
        private String phone;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleDto {
        private UUID id;
        private String name;
        private String plateNumber;
    }

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
