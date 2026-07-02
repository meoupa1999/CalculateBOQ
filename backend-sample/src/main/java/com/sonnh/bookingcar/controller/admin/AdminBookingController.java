package com.sonnh.bookingcar.controller.admin;

import com.sonnh.bookingcar.dto.request.admin.AdminCancelBookingReqDto;
import com.sonnh.bookingcar.dto.request.admin.AdminUpdateTourTimeReqDto;
import com.sonnh.bookingcar.dto.request.admin.BookingDispatchReqDto;
import com.sonnh.bookingcar.dto.response.admin.AdminBookingResDto;
import com.sonnh.bookingcar.dto.response.admin.AdminAirportBookingResDto;
import com.sonnh.bookingcar.dto.response.admin.AdminDashboardSummaryResDto;
import com.sonnh.bookingcar.dto.response.admin.AdminTourBookingResDto;
import com.sonnh.bookingcar.service.interfaces.AdminBookingService;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import com.sonnh.bookingcar.data.domain.enums.ServiceType;
import com.sonnh.bookingcar.dto.response.admin.ShiftResDto;
import com.sonnh.bookingcar.dto.response.admin.MilestoneResDto;
import com.sonnh.bookingcar.dto.response.tourist.TouristEstimateResDto;
import com.sonnh.bookingcar.dto.request.tourist.TripEstimateReqDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/bookings")
@RequiredArgsConstructor
@Tag(name = "Manage Bookings")
public class AdminBookingController {

    private final AdminBookingService adminBookingService;

    @GetMapping
    public ResponseEntity<PageImplResDto<AdminBookingResDto>> getBookings(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) ServiceType type) {
        return ResponseEntity.ok(adminBookingService.getBookings(page, size, search, status, type));
    }

    @GetMapping("/airport/{id}")
    public ResponseEntity<AdminAirportBookingResDto> getAirportBookingById(@PathVariable UUID id) {
        return ResponseEntity.ok(adminBookingService.getAirportBookingById(id));
    }

    @GetMapping("/tour/{id}")
    public ResponseEntity<AdminTourBookingResDto> getTourBookingById(@PathVariable UUID id) {
        return ResponseEntity.ok(adminBookingService.getTourBookingById(id));
    }

    @PostMapping("/dispatch/{id}")
    public ResponseEntity<Void> dispatchBooking(@PathVariable UUID id, @RequestBody BookingDispatchReqDto dto) {
        adminBookingService.dispatchBooking(id, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable UUID id, @RequestBody AdminCancelBookingReqDto dto) {
        adminBookingService.cancelRequest(id, dto.getReasonNote());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/available-shifts/{id}")
    public ResponseEntity<List<ShiftResDto>> getAvailableShifts(@PathVariable UUID id) {
        return ResponseEntity.ok(adminBookingService.getAvailableShiftsForBooking(id));
    }

    @GetMapping("/summary")
    public ResponseEntity<AdminDashboardSummaryResDto> getDashboardSummary() {
        return ResponseEntity.ok(adminBookingService.getDashboardSummary());
    }

    @PostMapping("/estimate")
    public ResponseEntity<TouristEstimateResDto> estimateTrip(@RequestBody TripEstimateReqDto dto) {
        return ResponseEntity.ok(adminBookingService.estimate(dto));
    }

    @GetMapping("/{id}/milestones")
    public ResponseEntity<List<MilestoneResDto>> getMilestones(@PathVariable UUID id) {
        return ResponseEntity.ok(adminBookingService.getMilestones(id));
    }

    @PutMapping("/update-tour-time/{id}")
    public ResponseEntity<Void> updateTourTime(@PathVariable UUID id, @RequestBody AdminUpdateTourTimeReqDto dto) {
        adminBookingService.updateRqTourTime(id, dto.getTime());
        return ResponseEntity.ok().build();
    }

}
