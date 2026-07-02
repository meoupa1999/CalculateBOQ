package com.sonnh.bookingcar.dto.response.admin;

import com.sonnh.bookingcar.data.domain.embedded.Audit;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class VehicleTypeResDto {
    private UUID id;
    private String name;
    private BigDecimal basePrice;
    private BigDecimal pricePerKm;
    private Double baseKm;
    private Boolean isDistanceBookingEnabled;
    private String description;
    private Audit audit;

    @Data
    public static class Audit {
        private String createdAt;
        private String updatedAt;
        private Boolean isActive;
    }
}
