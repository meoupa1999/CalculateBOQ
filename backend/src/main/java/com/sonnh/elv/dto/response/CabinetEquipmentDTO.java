package com.sonnh.elv.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CabinetEquipmentDTO {
    private Integer cameraQuantityInCabinet;
    private Integer from;
    private Integer to;
    private Integer sw24Quantity;
    private Integer sw16Quantity;
    private Integer ups;
    private Integer pdu;
    private Integer converter;
}
