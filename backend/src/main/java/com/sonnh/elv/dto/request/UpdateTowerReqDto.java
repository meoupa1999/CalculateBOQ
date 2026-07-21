package com.sonnh.elv.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTowerReqDto {
    private UUID configId;
    private String name;
    private Integer floorCount;
    private Integer basementCount;
    private Boolean hasRoof;
    private Double widthLength;
    private Double heightLength;
    private Integer quantity2U;
}
