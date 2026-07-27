package com.sonnh.elv.controller;

import com.sonnh.elv.dto.request.CreateProductRequestDTO;
import com.sonnh.elv.dto.response.ProductTypeResponseDTO;
import com.sonnh.elv.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Services")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Void> addProduct(@RequestBody CreateProductRequestDTO request) {
        productService.addProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/types")
    public ResponseEntity<List<ProductTypeResponseDTO>> getAllProductTypes() {
        return ResponseEntity.ok(productService.getAllProductTypes());
    }
}
