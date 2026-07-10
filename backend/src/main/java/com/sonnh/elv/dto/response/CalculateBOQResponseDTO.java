package com.sonnh.elv.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculateBOQResponseDTO {
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
    private String cabinetType;
}
