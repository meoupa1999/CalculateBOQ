package com.sonnh.bookingcar.controller.admin;

import com.sonnh.bookingcar.dto.request.admin.DriverCreateReqDto;
import com.sonnh.bookingcar.dto.request.admin.DriverUpdateReqDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.admin.DriverResDto;
import com.sonnh.bookingcar.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/drivers")
@RequiredArgsConstructor
@Tag(name = "Manage Driver")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<PageImplResDto<DriverResDto>> getAllDrivers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(userService.getAllDrivers(page, size, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResDto> getDriverById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getDriverById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DriverResDto> createDriver(
            @RequestPart("driver") DriverCreateReqDto dto,
            @RequestPart("driverImage") MultipartFile driverImage,
            @RequestPart("idCardFront") MultipartFile idCardFront,
            @RequestPart("idCardBack") MultipartFile idCardBack,
            @RequestPart("licenseImage") MultipartFile licenseImage) {
        return ResponseEntity.ok(userService.createDriver(dto, driverImage, idCardFront, idCardBack, licenseImage));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DriverResDto> updateDriver(
            @PathVariable UUID id,
            @RequestPart("driver") DriverUpdateReqDto dto,
            @RequestPart(value = "driverImage", required = false) MultipartFile driverImage,
            @RequestPart(value = "idCardFront", required = false) MultipartFile idCardFront,
            @RequestPart(value = "idCardBack", required = false) MultipartFile idCardBack,
            @RequestPart(value = "licenseImage", required = false) MultipartFile licenseImage) {
        return ResponseEntity.ok(userService.updateDriver(id, dto, driverImage, idCardFront, idCardBack, licenseImage));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable UUID id) {
        userService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }
}
