package com.sonnh.elv.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculateBOQRequestDTO {
    private Integer floorsCount;
    private Integer basementsCount;
    private Boolean hasRoof;
    private Double horizontalDistance;
    private Double verticalDistance;
    private String rackType;
    private Integer quantity2U;
    private List<FloorRequest> floors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FloorRequest {
        private Integer floorIndex;
        private String label;
        private Integer camerasCount;
        private Integer domeCount;
        private Integer bulletCount;
        private Integer cableLength;
    }
}
