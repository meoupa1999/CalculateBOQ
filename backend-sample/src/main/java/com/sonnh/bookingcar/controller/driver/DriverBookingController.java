package com.sonnh.bookingcar.controller.driver;

import com.sonnh.bookingcar.service.interfaces.DriverBookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sonnh.bookingcar.service.interfaces.AdminBookingService;
import com.sonnh.bookingcar.dto.response.admin.MilestoneResDto;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/driver/bookings")
@RequiredArgsConstructor
@Tag(name = "Manage Driver Bookings")
public class DriverBookingController {

    private final DriverBookingService driverBookingService;
    private final AdminBookingService adminBookingService;

    @PostMapping("/accept/{id}")
    public ResponseEntity<Void> acceptBooking(@PathVariable UUID id) {
        log.info("Driver accept booking request received for ID: {}", id);
        driverBookingService.acceptBooking(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<Void> rejectBooking(
            @PathVariable UUID id,
            @RequestParam(required = false) String reasonNote) {
        driverBookingService.rejectBooking(id, reasonNote);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/start/{id}")
    public ResponseEntity<Void> startBooking(@PathVariable UUID id) {
        driverBookingService.startBooking(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/complete/{id}")
    public ResponseEntity<Void> completeBooking(@PathVariable UUID id) {
        driverBookingService.completeBooking(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable UUID id,
            @RequestParam(required = false) String reasonNote) {
        driverBookingService.cancelBooking(id, reasonNote);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/milestones")
    public ResponseEntity<java.util.List<MilestoneResDto>> getMilestones(@PathVariable UUID id) {
        return ResponseEntity.ok(adminBookingService.getMilestones(id));
    }
}
