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
public class ProjectResponseDTO {
    private UUID id;
    private String name;
    private String description;
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
