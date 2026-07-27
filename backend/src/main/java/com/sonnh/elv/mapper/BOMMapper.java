package com.sonnh.elv.mapper;

import com.sonnh.elv.data.domain.Category;
import com.sonnh.elv.data.domain.Product;
import com.sonnh.elv.data.domain.ProductType;
import com.sonnh.elv.dto.response.CategoryResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BOMMapper {

    CategoryResponseDTO toCategoryResponseDTO(Category category);

    List<CategoryResponseDTO> toCategoryResponseDTOList(List<Category> categories);

    CategoryResponseDTO.ProductTypeResponseDTO toProductTypeResponseDTO(ProductType productType);

    CategoryResponseDTO.ProductResponseDTO toProductResponseDTO(Product product);
}
