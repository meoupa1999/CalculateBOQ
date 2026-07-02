package com.sonnh.bookingcar.controller.admin;

import com.sonnh.bookingcar.dto.request.admin.VehicleTypeReqDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.admin.VehicleCategoryResDto;
import com.sonnh.bookingcar.dto.response.admin.VehicleTypeResDto;
import com.sonnh.bookingcar.service.interfaces.VehicleTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/vehicle-types")
@RequiredArgsConstructor
@Tag(name = "Manage Vehicle Types")
public class AdminVehicleTypeController {
    private final VehicleTypeService vehicleTypeService;

    @GetMapping("/categories")
    public ResponseEntity<List<VehicleCategoryResDto>> getVehicleCategories() {
        return ResponseEntity.ok(vehicleTypeService.getAllCategories());
    }

    @PostMapping
    public ResponseEntity<VehicleTypeResDto> create(@RequestBody VehicleTypeReqDto dto) {
        return ResponseEntity.ok(vehicleTypeService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleTypeResDto> update(@PathVariable UUID id, @RequestBody VehicleTypeReqDto dto) {
        return ResponseEntity.ok(vehicleTypeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        vehicleTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleTypeResDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(vehicleTypeService.getById(id));
    }

    @GetMapping
    public ResponseEntity<PageImplResDto<VehicleTypeResDto>> getAll(
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(vehicleTypeService.getAll(searchKeyword, page, size));
    }
}
