package com.sonnh.elv.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.sonnh.elv.dto.request.CalculateBOQManualRequestDTO.CabinetAllocation;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculateBOQResponseDTO {
    private Integer floorIndex;
    private Integer fromIndex;
    private Integer toIndex;
    private Integer cabinetIndex;
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
    private String cabinetType;
    private List<CabinetDetailResponseDTO> cabinets;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CabinetDetailResponseDTO {
        private String cabinetId;
        private String cabinetType;
        private Integer cameraQuantityInCabinet;
        private Integer sw24Count;
        private Integer sw16Count;
        private Integer upsCount;
        private Integer pduCount;
        private Integer convCount;
        private List<CabinetAllocation> allocations;
    }
}

