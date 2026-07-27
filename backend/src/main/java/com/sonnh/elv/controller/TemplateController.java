package com.sonnh.elv.controller;

import com.sonnh.elv.dto.request.CreateTemplateReqDto;
import com.sonnh.elv.dto.response.TemplateResponseDto;
import com.sonnh.elv.service.TowerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
@Tag(name = "Manage Templates")
public class TemplateController {

    private final TowerService towerService;

    @PostMapping
    public ResponseEntity<Void> createTemplate(@RequestBody CreateTemplateReqDto request) {
        towerService.createTemplate(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<TemplateResponseDto>> getAllTemplates() {
        return ResponseEntity.ok(towerService.getAllTemplates());
    }
}
