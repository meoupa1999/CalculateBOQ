package com.sonnh.bookingcar.controller.tourist;

import com.sonnh.bookingcar.dto.request.tourist.AirportTransferReqDto;
import com.sonnh.bookingcar.dto.request.tourist.TourBookingReqDto;
import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import com.sonnh.bookingcar.data.domain.enums.ServiceType;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.tourist.TouristBookingDetailResDto;
import com.sonnh.bookingcar.dto.response.tourist.TouristBookingResDto;
import com.sonnh.bookingcar.service.interfaces.TouristRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sonnh.bookingcar.service.interfaces.AdminBookingService;
import com.sonnh.bookingcar.dto.response.admin.MilestoneResDto;
import com.sonnh.bookingcar.dto.response.tourist.TouristEstimateResDto;
import com.sonnh.bookingcar.dto.request.tourist.TripEstimateReqDto;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@RestController
@RequestMapping("/api/tourist/requests")
@RequiredArgsConstructor
@Tag(name = "Manage Tourist Requests")
@Slf4j
public class TouristRequestController {

    private final TouristRequestService touristRequestService;
    private final AdminBookingService adminBookingService;

    @PostMapping("/airport")
    public ResponseEntity<UUID> createAirportTransfer(@RequestBody AirportTransferReqDto dto) {
        UUID id = touristRequestService.createAirportTransfer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PostMapping("/tour")
    public ResponseEntity<UUID> createTourBooking(
            @RequestBody TourBookingReqDto dto) {
        UUID id = touristRequestService.createTourBooking(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable UUID id,
            @RequestParam(required = false) String reasonNote) {
        touristRequestService.cancelBooking(id, reasonNote);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<PageImplResDto<TouristBookingResDto>> getTouristBookings(
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) ServiceType type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(touristRequestService.getTouristBookings(searchKeyword, status, type, page, size));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<TouristBookingDetailResDto> getBookingDetail(@PathVariable UUID id) {
        return ResponseEntity.ok(touristRequestService.getBookingDetail(id));
    }

    @PostMapping("/estimate")
    public ResponseEntity<TouristEstimateResDto> estimateTrip(@RequestBody TripEstimateReqDto dto) {
        log.info("Received trip estimate request from frontend: {}", dto);
        return ResponseEntity.ok(touristRequestService.estimate(dto));
    }

    @GetMapping("/{id}/milestones")
    public ResponseEntity<java.util.List<MilestoneResDto>> getMilestones(@PathVariable UUID id) {
        return ResponseEntity.ok(adminBookingService.getMilestones(id));
    }
}
