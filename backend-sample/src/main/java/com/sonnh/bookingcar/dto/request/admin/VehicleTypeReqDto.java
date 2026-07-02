package com.sonnh.bookingcar.dto.request.admin;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class VehicleTypeReqDto {
    private String name;
    private BigDecimal basePrice;
    private BigDecimal pricePerKm;
    private Double baseKm;
    private Boolean isDistanceBookingEnabled;
    private String description;
}
