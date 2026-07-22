package com.sonnh.elv.controller;

import com.sonnh.elv.dto.request.CalculateBOQRequestDTO;
import com.sonnh.elv.dto.request.CalculateBOQManualRequestDTO;
import com.sonnh.elv.dto.request.CalculateBOMRequestDTO;
import com.sonnh.elv.dto.response.CalculateBOQResponseDTO;
import com.sonnh.elv.dto.response.CalculateBOMResponseDTO;
import com.sonnh.elv.service.CalculateService;
import com.sonnh.elv.service.CalculateBOMService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/calculate")
@RequiredArgsConstructor
@Tag(name = "Calculate Services")
public class CalculateController {

    private final CalculateService calculateService;
    private final CalculateBOMService calculateBOMService;
    private final com.sonnh.elv.service.ExcelExportService excelExportService;

    @GetMapping("/export-excel")
    public org.springframework.http.ResponseEntity<org.springframework.core.io.InputStreamResource> exportExcel(
            @RequestParam UUID projectId,
            @RequestParam(required = false) List<UUID> towerIds
    ) {
        java.io.ByteArrayInputStream in = excelExportService.exportProjectExcel(projectId, towerIds);
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=BOQ_Export.xlsx");
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return org.springframework.http.ResponseEntity
                .ok()
                .headers(headers)
                .body(new org.springframework.core.io.InputStreamResource(in));
    }

    @PostMapping("/cabinet-placement")
    public ResponseEntity<List<CalculateBOQResponseDTO>> getCabinetPlacement(
            @RequestParam(required = false) UUID towerId,
            @RequestBody CalculateBOQRequestDTO dto
    ) {
        List<CalculateBOQResponseDTO> placement = calculateService.calculateBOQ(towerId, dto);
        return ResponseEntity.ok(placement);
    }

    @PostMapping("/cabinet-placement-manual")
    public ResponseEntity<List<CalculateBOQResponseDTO>> getCabinetPlacementManual(
            @RequestParam(required = false) UUID towerId,
            @RequestBody CalculateBOQManualRequestDTO dto
    ) {
        List<CalculateBOQResponseDTO> placement = calculateService.calculateBOQManual(towerId, dto);
        return ResponseEntity.ok(placement);
    }

    @GetMapping("/cabinet-placement")
    public ResponseEntity<List<CalculateBOQResponseDTO>> getCalculateBOQ(
            @RequestParam UUID towerId
    ) {
        List<CalculateBOQResponseDTO> placement = calculateService.getCalculateBOQ(towerId);
        return ResponseEntity.ok(placement);
    }

    @PostMapping("/bom")
    public ResponseEntity<CalculateBOMResponseDTO> getBOM(
            @RequestBody List<CalculateBOMRequestDTO> dtos
    ) {
        CalculateBOMResponseDTO bom = calculateBOMService.calculateBOM(dtos);
        return ResponseEntity.ok(bom);
    }
}
