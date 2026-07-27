package com.sonnh.elv.service;

import com.sonnh.elv.dto.request.CreateProductRequestDTO;
import com.sonnh.elv.dto.response.ProductTypeResponseDTO;
import java.util.List;

public interface ProductService {
    void addProduct(CreateProductRequestDTO request);
    List<ProductTypeResponseDTO> getAllProductTypes();
}
