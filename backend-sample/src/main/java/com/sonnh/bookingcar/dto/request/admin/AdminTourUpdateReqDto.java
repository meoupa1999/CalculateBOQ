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
public class AdminTourUpdateReqDto {
    private String tourName;
    private String description;
    private String shortDescription;
    private String imageUrl;
    private String base64Image;
    private Long duration;
    private java.time.LocalTime defaultPickupTime;
    private List<VehiclesTypePriceDto> vehiclePrices;
    private List<ItineraryReqDto> itineraries;
    private List<SpecialLocationReqDto> specialLocations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpecialLocationReqDto {
        private UUID id;
        private Integer priority;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehiclesTypePriceDto {
        private UUID vehiclesTypePriceId;
        private BigDecimal price;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItineraryReqDto {
        private String time;
        private String title;
        private String description;
        private Integer orderIndex;
    }
}
