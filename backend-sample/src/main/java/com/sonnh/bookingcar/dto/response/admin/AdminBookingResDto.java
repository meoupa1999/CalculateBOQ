package com.sonnh.bookingcar.dto.response.admin;

import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import com.sonnh.bookingcar.data.domain.enums.ServiceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminBookingResDto {
    private UUID id;
    private String bookingCode;
    private String customerName;
    private String customerPhone;
    private ServiceType type;
    private String location;
    private String date;
    private String time;
    private BookingStatus status;
    private String createdAt;
    private String driverName;
    private String vehicleInfo;
    private String pickupLocation;
    private String dropoffLocation;
    private Integer passengers;
    private String notes;
    private String description;
    private String vehicleTypeRequested;
    private String paymentMethod;
    private BigDecimal totalPrice;
    private Double pickupLat;
    private Double pickupLon;
    private Double dropoffLat;
    private Double dropoffLon;
    private AdminTourResDto tour;
    private LocalDateTime estimatedEndDate;
    @JsonProperty("currentShiftId")
    private UUID currentShiftId;
}
