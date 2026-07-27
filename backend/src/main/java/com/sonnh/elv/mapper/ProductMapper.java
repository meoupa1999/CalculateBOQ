package com.sonnh.elv.mapper;

import com.sonnh.elv.data.domain.Product;
import com.sonnh.elv.data.domain.ProductType;
import com.sonnh.elv.dto.request.CreateProductRequestDTO;
import com.sonnh.elv.dto.response.ProductTypeResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(source = "product.name", target = "name")
    @Mapping(source = "product.description", target = "description")
    Product toProduct(CreateProductRequestDTO dto);

    ProductTypeResponseDTO toProductTypeResponseDTO(ProductType productType);

    List<ProductTypeResponseDTO> toProductTypeResponseDTOList(List<ProductType> productTypes);
}
