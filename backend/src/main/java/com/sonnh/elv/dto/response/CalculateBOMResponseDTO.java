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
    private Integer camDomeQuantity; // Camera IP Dome 2MP HIKVISION DS-2CD1121G0-I
    private Integer camBulletQuantity; // Camera IP thân 2MP HIKVISION DS-2CD1021G0-I
    private Integer recorder16Quantity;// Đầu ghi hình camera IP 16 kênh HIKVISION DS-7716NXI-K4
    private Integer recorder32Quantity;// Đầu ghi hình camera IP 16 kênh
    private Integer hardDiskQuantity; // Ổ Cứng 10T WESTERN
    private Integer swich16POEQuantity; // Switch POE 16 port 250W Hikvision DS-3E0516P-EI
    private Integer swich24POEQuantity; // Switch Hikvision POE 24 cổng DS-3E1326P-EI
    private Integer swich16CISCOQuantity;// Switch 16 port CISCO CBS110-16T-EU
    private Integer swich24CISCOQuantity; // Switch 24 port CISCO CBS120-24T-EU
    private Integer obserScreenQuantity;// Màn hình quan sát 43 inch SamSung(khung kê + HDMI (15m))
    private Integer fiberCableQuantity; // Cáp quang 4FO (tự nhập)
    private Integer cableQuantity; // Cáp mạng Cat5E (tự nhập)
    private Integer converterQuantity; // Bộ chuyển đổi quang điện Gigabit GNETCOM 10/100/1000M GNC-2111S-20A/B
    private Integer cabinet2UQuantity; // Tủ rack 2U
    private Integer cabinet6UQuantity; // Tủ rack 6U
    private Integer cabinet10UQuantity; // Tủ rack 10U
    private Integer cabinet20UQuantity; // Tủ rack 20U
    private Integer cabinet32UQuantity; // Tủ rack 32U
    private Integer cabinet42UQuantity; // Tủ rack 42U
    private Integer odf12FOQuantity;// ODF 12FO SC/UPC (Full Phụ kiện)
    private Integer odf24FOQuantity;// ODF 24FO SC/UPC (Full Phụ kiện)
    private Integer cvvCable; // Dây điện CVV 2x2.5
    private Integer pduQuantity; // Thanh nguồn PDU đa năng 6 ổ cắm 3 chấu chuẩn 19"
    private Integer ups1000Quantity; // Nguồn lưu điện UPS ARES Model AR610 1000VA/800W
    private Integer ups3000Quantity;// Nguồn lưu điện UPS ARES Model AR630 3000VA-2400W ( tự nhập)
    private Integer ampCatQuantity; // Đầu mạng AMP Cat 5
    private Integer fiberOpticalPatchQuantity; // Dây nhảy quang SC/UPC SC/UPC 3M
    private Integer odf4FOQuantity; // ODF 4FO SC/UPC - SC/UPC (Full phụ kiện)
    private Integer patchCordQuantity; // Dây nhảy mạng Cat5
    private Integer cablemanageQuantity; // Thanh quản lý cáp mạng 19inch
    private Integer chickenTubeQuantity;// Ruột gà phi 20
    private Integer electricTubeQuantity; // Ống điện D20
}
