package com.sonnh.bookingcar.dto.request.tourist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourBookingReqDto {
    private UUID tourId;
    private UUID vehiclePriceId;
    private String pickupLocation;
    
    // ISO format YYYY-MM-DD
    private LocalDate pickupDate;
    
    private Integer numberOfPeople;
    private String notes;
    private String paymentMethod;
    private BigDecimal totalPrice;
}
