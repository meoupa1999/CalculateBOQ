package com.sonnh.bookingcar.dto.request.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminTourReqDto {
    private String tourName;
    private String description;
    private String shortDescription;
    private String imageUrl;
    private String base64Image;
    private Long duration;
    private java.time.LocalTime defaultPickupTime;
    private List<VehiclesTypePriceDto> vehiclePrices;
    private List<ItineraryReqDto> itineraries;
    // private List<HighlightReqDto> highlights;
    private List<SpecialLocationReqDto> specialLocations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpecialLocationReqDto {
        private java.util.UUID id;
        private Integer priority;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehiclesTypePriceDto {
        private BigDecimal price;
        private java.util.UUID vehicleTypeId;
    }

    // @Data
    // @Builder
    // @NoArgsConstructor
    // @AllArgsConstructor
    // public static class HighlightReqDto {
    // private String title;
    // private String description;
    // private Integer orderIndex;
    // }

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
