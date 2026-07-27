package com.sonnh.elv.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BOMBatchRequestDTO {

    private List<CreateItemDTO> create;
    private List<UpdateItemDTO> update;
    private List<UUID> delete;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateItemDTO {
        private String name;
        private UUID categoryId;
        private String unit;
        private Double labor;
        private Double orderIndex;
        private String formula;
        private String note;
        private String code;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateItemDTO {
        private UUID id;
        private String name;
        private String unit;
        private Double labor;
        private Double orderIndex;
        private String formula;
        private String note;
        private String code;
    }
}
