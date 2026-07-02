package com.sonnh.bookingcar.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminTourResDto {
    private UUID id;
    private String tourName;
    private String description;
    private String shortDescription;
    private String imageUrl;
    private String base64Image;
    private Long duration;
    private LocalTime defaultPickupTime;
    private String createdAt;
    private BigDecimal price;
    private List<VehiclesTypePriceDto> vehiclePrices;
    private List<ItineraryResDto> itineraries;
    // private List<HighlightResDto> highlights;
    private List<SpecialLocationMappingResDto> specialLocationMappings;
    private List<CheckinTourHistoryResDto> checkinTourHistories;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpecialLocationTourDto {
        private UUID tourId;
        private UUID specialLocationId;
        private Integer priority;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpecialLocationMappingResDto {
        private UUID id;
        private UUID specialLocationId;
        private String name;
        private Integer priority;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SpecialLocationResDto {
        private UUID id;
        private String locationName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehiclesTypePriceDto {
        private UUID id;
        private BigDecimal price;
        private VehicleTypeDto vehicleType;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleTypeDto {
        private UUID id;
        private String name;
    }

    // @Data
    // @Builder
    // @NoArgsConstructor
    // @AllArgsConstructor
    // public static class HighlightResDto {
    // private UUID id;
    // private String title;
    // private String description;
    // private Integer orderIndex;
    // }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItineraryResDto {
        private UUID id;
        private String time;
        private String title;
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckinTourHistoryResDto {
        private UUID checkinTourId;
        private String status;
        private String reasonNote;
        private String createdAt;
        private DriverDto driver;
        private SpecialLocationMappingResDto specialLocationTour;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DriverDto {
        private UUID id;
        private String fullName;
        private String phone;
        private String email;
    }
}
