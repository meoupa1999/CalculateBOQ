package com.sonnh.bookingcar.dto.response.admin;

import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import com.sonnh.bookingcar.data.domain.enums.ServiceType;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class AdminTourBookingResDto {
    private UUID id;
    private String bookingCode;
    private ServiceType type;
    private String pickupLocation;
    private String date;
    private String time;
    private Integer passengers; // number of people
    private String notes;
    private String paymentMethod;
    private BigDecimal totalPrice;
    private AdminTourResDto tour;
    private Double dropoffLon;
    private BookingStatus status;
    private String createdAt;
    private String driverName;
    private TouristTourDto tourist;
    private String vehicleInfo;
    private java.time.LocalDateTime estimatedEndDate;

    @JsonProperty("currentShift")
    private ShiftDto currentShift;

    private CancelReasonResDto cancelReason;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TouristTourDto {
        private UUID id;
        private String fullName;
        private String phone;
        private String email;
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
