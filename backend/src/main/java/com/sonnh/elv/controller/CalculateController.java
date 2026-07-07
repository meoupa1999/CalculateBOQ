package com.sonnh.elv.controller;

import com.sonnh.elv.dto.request.CalculateBOQRequestDTO;
import com.sonnh.elv.dto.response.CalculateBOQResponseDTO;
import com.sonnh.elv.dto.response.CalculateBOMResponseDTO;
import com.sonnh.elv.service.CalculateService;
import com.sonnh.elv.service.CalculateBOMService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calculate")
@RequiredArgsConstructor
@Tag(name = "Calculate Services")
public class CalculateController {

    private final CalculateService calculateService;
    private final CalculateBOMService calculateBOMService;

    @PostMapping("/cabinet-placement")
    public ResponseEntity<List<CalculateBOQResponseDTO>> getCabinetPlacement(
            @RequestBody CalculateBOQRequestDTO dto
    ) {
        List<CalculateBOQResponseDTO> placement = calculateService.calculateBOQ(dto);
        return ResponseEntity.ok(placement);
    }

    @PostMapping("/bom")
    public ResponseEntity<CalculateBOMResponseDTO> getBOM(
            @RequestBody CalculateBOQRequestDTO dto
    ) {
        CalculateBOMResponseDTO bom = calculateBOMService.calculateBOM(dto);
        return ResponseEntity.ok(bom);
    }
}
