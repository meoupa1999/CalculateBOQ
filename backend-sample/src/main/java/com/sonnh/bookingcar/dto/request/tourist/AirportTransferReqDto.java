package com.sonnh.bookingcar.dto.request.tourist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirportTransferReqDto {
    private String pickupLocation;
    private String shortPickupLocation;
    private String dropoffLocation;
    private String shortDropoffLocation;
    private LocalDate pickupDate;
    private LocalTime pickupTime;
    private Integer passengers;
    private UUID vehicleTypeId;
    private String notes;
    private String description;
    private String paymentMethod;
    private BigDecimal estimatedPrice;
    private Double pickupLat;
    private Double pickupLon;
    private Double dropoffLat;
    private Double dropoffLon;
}
