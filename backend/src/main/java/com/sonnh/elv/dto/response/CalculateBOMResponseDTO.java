package com.sonnh.elv.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculateBOMResponseDTO {
    private Integer camDomeQuantity;
    private Integer camBulletQuantity;
    private Integer recorder16Quantity;
    private Integer recorder32Quantity;
    private Integer hardDiskQuantity;
    private Integer swich16POEQuantity;
    private Integer swich24POEQuantity;
    private Integer swich16CISCOQuantity;
    private Integer swich24CISCOQuantity;
    private Integer obserScreenQuantity;
    private Integer fiberCableQuantity;
    private Integer cableQuantity;
    private Integer converterQuantity;
    private Integer cabinet2UQuantity;
    private Integer cabinet6UQuantity;
    private Integer cabinet10UQuantity;
    private Integer cabinet20UQuantity;
    private Integer cabinet32UQuantity;
    private Integer cabinet42UQuantity;
    private Integer odfQuantity;
    private Integer cvvCable;
    private Integer pduQuantity;
    private Integer ups1000Quantity;
    private Integer ups3000Quantity;
    private Integer ampCatQuantity;
    private Integer fiberOpticalPatchQuantity;
    private Integer patchCordQuantity;
    private Integer cablemanageQuantity;
    private Integer chickenTubeQuantity;
    private Integer electricTubeQuantity;
}
