package com.sonnh.bookingcar.controller.driver;

import com.sonnh.bookingcar.dto.request.driver.DriverCheckinReqDto;
import com.sonnh.bookingcar.service.interfaces.DriverCheckinService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import com.sonnh.bookingcar.dto.response.admin.AdminTourResDto;

import java.util.UUID;

@RestController
@RequestMapping("/api/driver/checkin")
@RequiredArgsConstructor
@Tag(name = "Driver Checkin")
public class DriverCheckinController {

    private final DriverCheckinService driverCheckinService;

    // Hardcoded Driver ID for now as requested
    private static final String HARDCODED_DRIVER_ID = "68134fe6-287b-453f-a709-45719e4c35f1";

    @PostMapping
    public ResponseEntity<Void> checkin(@RequestBody DriverCheckinReqDto dto) {
        UUID driverId = UUID.fromString(HARDCODED_DRIVER_ID);
        driverCheckinService.checkin(driverId, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/locations")
    public ResponseEntity<List<AdminTourResDto.SpecialLocationMappingResDto>> getSpecialLocationTours(
            @RequestParam UUID tourId) {
        return ResponseEntity.ok(driverCheckinService.getSpecialLocationTours(tourId));
    }
}
