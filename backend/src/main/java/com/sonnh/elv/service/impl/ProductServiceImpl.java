package com.sonnh.elv.service.impl;

import com.sonnh.elv.data.domain.Product;
import com.sonnh.elv.data.domain.ProductType;
import com.sonnh.elv.data.repository.ProductRepository;
import com.sonnh.elv.data.repository.ProductTypeRepository;
import com.sonnh.elv.dto.request.CreateProductRequestDTO;
import com.sonnh.elv.dto.response.ProductTypeResponseDTO;
import com.sonnh.elv.service.ProductService;
import com.sonnh.elv.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ProductMapper productMapper;

    @Override
    public void addProduct(CreateProductRequestDTO request) {
        ProductType productType = productTypeRepository.findById(request.getProductTypeId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "ProductType not found with id: " + request.getProductTypeId()));

        Product product = productMapper.toProduct(request);
        product.addProductType(productType);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductTypeResponseDTO> getAllProductTypes() {
        List<ProductType> productTypes = productTypeRepository.findAll();
        return productMapper.toProductTypeResponseDTOList(productTypes);
    }
}
