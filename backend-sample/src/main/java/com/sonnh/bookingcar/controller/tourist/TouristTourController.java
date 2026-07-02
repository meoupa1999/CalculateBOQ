package com.sonnh.bookingcar.controller.tourist;

import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.admin.AdminTourResDto;
import com.sonnh.bookingcar.service.interfaces.TouristTourService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/tourist/tours")
@RequiredArgsConstructor
@Tag(name = "Tourist Tour APIs", description = "Endpoints for tourists to explore and view tours")
public class TouristTourController {

    private final TouristTourService touristTourService;

    @GetMapping
    @Operation(summary = "Get list of active tours")
    public ResponseEntity<PageImplResDto<AdminTourResDto>> getTours(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(touristTourService.getActiveTours(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get active tour detail by id")
    public ResponseEntity<AdminTourResDto> getTourById(@PathVariable UUID id) {
        return ResponseEntity.ok(touristTourService.getTourDetail(id));
    }
}
