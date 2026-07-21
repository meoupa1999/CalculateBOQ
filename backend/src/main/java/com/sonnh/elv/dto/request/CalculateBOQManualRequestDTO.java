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
    private Integer quantity2U;
    private List<FloorRequest> floors;
    private List<ManualCabinetGroup> manualGroups;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ManualCabinetGroup {
        private Integer cabinetIndex; // tầng user đặt tủ
        private Map<Integer, Integer> floorRange; // khoảng index user nhóm tầng ví dụ {"4": 9}
        private List<Cabinet> cabinets; // danh sách các tủ đặt tại tầng này
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cabinet {
        private String id;
        private String type; // "2U", "6U", ...
        private Integer totalDome;
        private Integer totalBullet;
        private Integer totalCamera;
        private List<CabinetAllocation> allocations;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CabinetAllocation {
        private Integer floorIndex;
        private Integer domeCount;
        private Integer bulletCount;
    }
}
