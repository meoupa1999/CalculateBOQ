package com.sonnh.bookingcar.dto.response.tourist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TouristEstimateResDto {
    private double distance;
    private int duration;
    private String geometry;
    private int averageSpeed;
    private List<VehiclesPricingDto> vehicles;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehiclesPricingDto {
        private UUID id;
        private String name;
        private double price;
    }
}
