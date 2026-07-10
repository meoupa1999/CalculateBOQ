package com.sonnh.elv.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.sonnh.elv.dto.request.CalculateBOQRequestDTO.FloorRequest;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculateBOQManualRequestDTO {
    private Integer floorsCount;
    private Integer basementsCount;
    private Boolean hasRoof;
    private Double horizontalDistance;
    private Double verticalDistance;
    private String rackType;
    private List<FloorRequest> floors;
    private List<ManualCabinetGroup> manualGroups;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ManualCabinetGroup {
        private Integer cabinetIndex; // tầng user đặt tủ
        private Integer totalCamera; // tổng cam tính tổng trong nhóm các tầng user chọn luôn
        private Map<Integer, Integer> floorRange; // khoảng index user nhóm tầng ví dụ {"4": 9}
        private String rackType; // loại tủ của riêng nhóm này
    }
}
