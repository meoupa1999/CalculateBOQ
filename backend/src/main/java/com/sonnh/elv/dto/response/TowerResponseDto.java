package com.sonnh.elv.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TowerResponseDto {
    private UUID id;
    private String name;
    private Integer floorCount;
    private Integer basementCount;
    private Boolean hasRoof;
    private Double widthLength;
    private Double heightLength;
    private String specialName;
    private UUID projectId;
    private UUID configId;
    private AuditDto audit;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuditDto {
        private String createdBy;
        private String createdAt;
        private String updatedBy;
        private String updatedAt;
        private Boolean isActive;
    }
}
