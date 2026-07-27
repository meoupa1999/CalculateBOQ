package com.sonnh.elv.controller;

import com.sonnh.elv.dto.response.CategoryResponseDTO;
import com.sonnh.elv.service.BOMCategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "BOM Category Services")
public class BOMCategoryController {

    private final BOMCategoryService bomCategoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getCategoryTree() {
        return ResponseEntity.ok(bomCategoryService.getCategoryTree());
    }
}
