package com.sonnh.bookingcar.dto.request.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourPriceUpdateDto {
    private UUID tourId;
    private List<VehiclesTypePriceUpdateDto> vehiclePrices;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehiclesTypePriceUpdateDto {
        private UUID vehiclesTypePriceId;
        private BigDecimal price;
    }
}
