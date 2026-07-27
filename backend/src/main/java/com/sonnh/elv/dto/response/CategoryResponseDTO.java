package com.sonnh.elv.dto.response;

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
public class CategoryResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private Double orderIndex;
    private List<CategoryResponseDTO> children;
    private List<ProductTypeResponseDTO> productTypes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductTypeResponseDTO {
        private UUID id;
        private String code;
        private String name;
        private String formula;
        private Double orderIndex;
        private String note;
        private String unit;
        private Double labor;
        private List<ProductResponseDTO> products;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductResponseDTO {
        private UUID id;
        private String name;
        private String description;
    }
}
