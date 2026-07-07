package com.sonnh.elv.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculateBOQResponseDTO {
    private Integer totalCamera;
    private Integer totalCamDome;
    private Integer totalCamBullet;
    private Integer totalSwichPOE;
    private Integer totalSw16;
    private Integer totalSw24;
    private Integer totalCabinet;
    private Integer totalUPS;
    private Integer totalPDU;
    private Integer totalConverter;
    private List<FloorBOQResponse> floors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FloorBOQResponse {
        private Integer floorIndex;
        private Integer fromIndex;
        private Integer toIndex;
        private Boolean isCabinetPlaced;
        private String label;
        private Integer camerasCount;
        private Integer domeCount;
        private Integer bulletCount;
        private Integer cableLength;
        private Integer cameraQuantityInCabinet;
        private Integer sw24Count;
        private Integer sw16Count;
        private Integer upsCount;
        private Integer pduCount;
        private Integer convCount;
    }
}
