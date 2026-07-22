package com.sonnh.elv.dto.excel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BOQRowExcelDTO {
    private String floorName;       // TANG: Tên tầng (35, 34..., B1, B2...)
    private Integer cameraDome;     // Camera DOME
    private Integer cameraBullet;   // CAMERA THAN
    private Integer totalCamera;    // Tổng CAM
    private Integer cameraToCabinet;// CAM VỀ TỦ
    private Integer cabinet2U;      // TU 2U
    private Integer cabinet6U;      // TU 6U
    private Integer sw24;           // SW 24
    private Integer sw16;           // SW 16
    private Integer ups;            // UPS AR610 1K
    private Integer pdu;            // PDU
    private Integer converter;      // Converter
    private Double cableLength;     // Mét cáp
}
