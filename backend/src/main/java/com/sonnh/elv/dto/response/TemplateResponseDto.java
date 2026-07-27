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
public class TemplateResponseDto {
    private UUID id;
    private String name;
    private String description;
    private List<ProductDto> products;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDto {
        private UUID id;
        private String name;
        private String description;
        private String productTypeCode;
    }
}
