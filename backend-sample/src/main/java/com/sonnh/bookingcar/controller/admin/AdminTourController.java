package com.sonnh.bookingcar.controller.admin;

import com.sonnh.bookingcar.dto.request.admin.AdminTourReqDto;
import com.sonnh.bookingcar.dto.request.admin.AdminTourUpdateReqDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.admin.AdminTourResDto;
import com.sonnh.bookingcar.service.interfaces.AdminTourService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import com.sonnh.bookingcar.dto.request.admin.TourPriceUpdateDto;

@RestController
@RequestMapping("/api/admin/tours")
@RequiredArgsConstructor
@Tag(name = "Manage Tour")
public class AdminTourController {

    private final AdminTourService adminTourService;

    @PostMapping
    public ResponseEntity<AdminTourResDto> createTour(@RequestBody AdminTourReqDto dto) {
        return ResponseEntity.ok(adminTourService.createTour(dto));
    }

    @GetMapping
    public ResponseEntity<PageImplResDto<AdminTourResDto>> getTours(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String search) {
        int pageIndex = (page == null || page < 1) ? 0 : page - 1;
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(Sort.Direction.DESC, "audit.updatedAt"));
        return ResponseEntity.ok(adminTourService.getTours(search, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminTourResDto> getTourById(@PathVariable UUID id) {
        return ResponseEntity.ok(adminTourService.getTourById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminTourResDto> updateTour(@PathVariable UUID id, @RequestBody AdminTourUpdateReqDto dto) {
        return ResponseEntity.ok(adminTourService.updateTour(id, dto));
    }

    @PutMapping("/bulk-prices")
    public ResponseEntity<Void> bulkUpdatePrices(@RequestBody List<TourPriceUpdateDto> dtos) {
        adminTourService.bulkUpdatePrices(dtos);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable UUID id) {
        adminTourService.deleteTour(id);
        return ResponseEntity.noContent().build();
    }
}
