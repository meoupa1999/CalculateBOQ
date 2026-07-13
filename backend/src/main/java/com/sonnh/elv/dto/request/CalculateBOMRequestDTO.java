package com.sonnh.elv.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculateBOMRequestDTO {
    private UUID towerId;
    private Integer totalCamera;
    private Integer totalCamDome;
    private Integer totalCamBullet;
    private Integer totalSwichPOE;
    private Integer totalSw16;
    private Integer totalSw24;
    private Map<String, Integer> cabinets;
    private Integer totalUPS;
    private Integer totalPDU;
    private Integer totalConverter;
    private List<FloorBOMInfo> floors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FloorBOMInfo {
        private Integer floorIndex;
        private Boolean isCabinetPlaced;
        private String label;
        private Integer camerasCount;
        private Integer domeCount;
        private Integer bulletCount;
        private Integer cameraQuantityInCabinet;
        private Integer sw24Count;
        private Integer sw16Count;
        private Integer upsCount;
        private Integer pduCount;
        private Integer convCount;
        private String cabinetType;
    }
}
