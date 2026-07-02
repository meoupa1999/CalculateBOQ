package com.sonnh.bookingcar.controller.admin;

import com.sonnh.bookingcar.dto.request.admin.VehicleCreateReqDto;
import com.sonnh.bookingcar.dto.request.admin.VehicleUpdateReqDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.VehicleDashboardDTO;
import com.sonnh.bookingcar.dto.response.admin.VehicleResDto;
import com.sonnh.bookingcar.service.interfaces.VehicleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


import java.util.List;

@RestController
@RequestMapping("/api/admin/vehicles")
@RequiredArgsConstructor
@Tag(name = "Manage Vehicles")
public class AdminVehicleController {
    private final VehicleService vehicleService;


    @GetMapping
    public ResponseEntity<PageImplResDto<VehicleResDto>> getAllVehicles(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean missingDocuments
    ) {
        return ResponseEntity.ok(vehicleService.getAllVehicles(page, size, search, missingDocuments));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<VehicleDashboardDTO> getVehicleDashboard() {
        return ResponseEntity.ok(vehicleService.getVehicleDashboard());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResDto> getVehicleById(@PathVariable UUID id) {
        return ResponseEntity.ok(vehicleService.getById(id));
    }

    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VehicleResDto> createVehicle(
            @RequestPart("vehicle") VehicleCreateReqDto dto,
            @RequestPart(value = "insuranceImage", required = false) org.springframework.web.multipart.MultipartFile insuranceImage,
            @RequestPart(value = "registrationImage", required = false) org.springframework.web.multipart.MultipartFile registrationImage,
            @RequestPart(value = "badgeImage", required = false) org.springframework.web.multipart.MultipartFile badgeImage,
            @RequestPart(value = "vehicleImage", required = false) org.springframework.web.multipart.MultipartFile vehicleImage
    ) {
        return ResponseEntity.ok(vehicleService.create(dto, insuranceImage, registrationImage, badgeImage, vehicleImage));
    }

    @PutMapping(value = "/{id}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VehicleResDto> updateVehicle(
            @PathVariable UUID id,
            @RequestPart("vehicle") VehicleUpdateReqDto dto,
            @RequestPart(value = "insuranceImage", required = false) org.springframework.web.multipart.MultipartFile insuranceImage,
            @RequestPart(value = "registrationImage", required = false) org.springframework.web.multipart.MultipartFile registrationImage,
            @RequestPart(value = "badgeImage", required = false) org.springframework.web.multipart.MultipartFile badgeImage,
            @RequestPart(value = "vehicleImage", required = false) org.springframework.web.multipart.MultipartFile vehicleImage
    ) {
        return ResponseEntity.ok(vehicleService.update(id, dto, insuranceImage, registrationImage, badgeImage, vehicleImage));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable UUID id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
