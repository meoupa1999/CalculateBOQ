package com.sonnh.bookingcar.controller.driver;

import com.sonnh.bookingcar.dto.response.driver.DriverAirportDetailResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverRequestResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverTourDetailResDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.admin.ShiftResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverSystemRequestResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverShiftDetailResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverProfileResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverEarningsResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverVehicleDetailResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverDocumentResDto;
import com.sonnh.bookingcar.dto.response.driver.WorkDaysResDto;
import com.sonnh.bookingcar.dto.request.driver.UpdateProfileReqDto;
import com.sonnh.bookingcar.dto.request.driver.StartShiftReqDto;
import com.sonnh.bookingcar.dto.request.driver.EndShiftReqDto;
import com.sonnh.bookingcar.dto.request.driver.NegotiatePriceReqDto;
import com.sonnh.bookingcar.dto.request.driver.AddSurchargeReqDto;
import com.sonnh.bookingcar.service.interfaces.DriverMobileService;
import com.sonnh.bookingcar.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
public class DriverMobileController {

    private final DriverMobileService driverMobileService;

    @Tag(name = "Driver Mobile")
    @GetMapping("/requests")
    public PageImplResDto<DriverRequestResDto> getMyRequests(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        UUID driverId = SecurityUtils.getCurrentUserId();
        return driverMobileService.getAssignedRequests(driverId, startDate, endDate, page, size);
    }

    @Tag(name = "Driver Mobile")
    @GetMapping("/requests/history")
    public List<DriverRequestResDto> getMyHistory() {
        UUID driverId = SecurityUtils.getCurrentUserId();
        return driverMobileService.getBookingHistory(driverId);
    }

    @Tag(name = "Driver Mobile")
    @GetMapping("/shifts")
    public ShiftResDto getMyShifts() {
        UUID driverId = SecurityUtils.getCurrentUserId();
        return driverMobileService.getMyShifts(driverId);
    }

    @Tag(name = "Driver Mobile")
    @GetMapping("/requests/airport/{requestId}")
    public DriverAirportDetailResDto getAirportDetail(@PathVariable UUID requestId) {
        return driverMobileService.getAirportRequestDetail(requestId);
    }

    @Tag(name = "Driver Mobile")
    @GetMapping("/requests/tour/{requestId}")
    public DriverTourDetailResDto getTourDetail(@PathVariable UUID requestId) {
        return driverMobileService.getTourRequestDetail(requestId);
    }

    @Tag(name = "Driver Mobile")
    @PatchMapping("/shifts/start/{shiftId}")
    public ResponseEntity<Void> startShift(@PathVariable UUID shiftId, @RequestBody StartShiftReqDto dto) {
        dto.setShiftId(shiftId);
        driverMobileService.startShift(dto);
        return ResponseEntity.ok().build();
    }

    @Tag(name = "Driver Mobile")
    @PostMapping("/shifts/end/{shiftId}")
    public ResponseEntity<Void> endShift(@PathVariable UUID shiftId, @RequestBody EndShiftReqDto dto) {
        dto.setShiftId(shiftId);
        driverMobileService.endShift(dto);
        return ResponseEntity.ok().build();
    }

    @Tag(name = "Driver Mobile")
    @PatchMapping("/shifts/confirm_early_closure/{shiftId}")
    public ResponseEntity<Void> confirmEarlyClosure(
            @PathVariable UUID shiftId,
            @RequestParam UUID historyId) {
        driverMobileService.confirmEarlyClosure(shiftId, historyId);
        return ResponseEntity.ok().build();
    }

    @Tag(name = "Driver Mobile")
    @GetMapping("/system-requests")
    public List<DriverSystemRequestResDto> getSystemRequests() {
        UUID driverId = SecurityUtils.getCurrentUserId();
        return driverMobileService.getSystemRequests(driverId);
    }

    @Tag(name = "Driver Mobile")
    @GetMapping("/shifts/detail/{shiftId}")
    public DriverShiftDetailResDto getShiftDetail(@PathVariable UUID shiftId) {
        return driverMobileService.getShiftDetail(shiftId);
    }

    // @Tag(name = "Tracking Location")
    // @PostMapping("/location")
    // @Operation(summary = "Lấy vị trí của tài xế")
    // public ResponseEntity<Void> updateLocation(@RequestBody DriverLocationReqDto
    // dto) {
    // // Now using the driverId from the DTO
    // driverMobileService.updateLocation(dto);
    // return ResponseEntity.ok().build();
    // }

    // @Tag(name = "Tracking Location")
    // @GetMapping("/location")
    // @Operation(summary = "Lấy tất cả vị trí của tài xế")
    // public List<DriverLocationResDto> getAllDriverLocation(
    // @RequestParam(defaultValue = "0") double lat,
    // @RequestParam(defaultValue = "0") double lng) {
    // return driverMobileService.getAllDriverLocation(lat, lng);
    // }

    // @Tag(name = "Tracking Location")
    // @GetMapping("/location/{driverId}")
    // @Operation(summary = "Lấy vị trí của một tài xế")
    // public DriverLocationResDto getDriverLocation(@PathVariable UUID driverId) {
    // return driverMobileService.getDriverLocation(driverId);
    // }

    @Tag(name = "Tracking Location")
    @PostMapping("/tracking/start/{driverId}")
    @Operation(summary = "Bắt đầu lấy vị trí của tài xế")
    public ResponseEntity<Void> startTracking(@PathVariable UUID driverId) {
        driverMobileService.startTracking(driverId);
        return ResponseEntity.ok().build();
    }

    @Tag(name = "Tracking Location")
    @PostMapping("/tracking/stop/{driverId}")
    @Operation(summary = "Dừng lấy vị trí của tài xế")
    public ResponseEntity<Void> stopTracking(@PathVariable UUID driverId) {
        driverMobileService.stopTracking(driverId);
        return ResponseEntity.ok().build();
    }

    @Tag(name = "Tracking Location")
    @PostMapping("/tracking/global/start")
    public ResponseEntity<Void> startGlobalTracking() {
        driverMobileService.startGlobalTracking();
        return ResponseEntity.ok().build();
    }

    @Tag(name = "Tracking Location")
    @PostMapping("/tracking/global/stop")
    public ResponseEntity<Void> stopGlobalTracking() {
        driverMobileService.stopGlobalTracking();
        return ResponseEntity.ok().build();
    }

    // @Tag(name = "Tracking Location")
    // @GetMapping("/tracking/status")
    // @Operation(summary = "Lấy trạng thái lấy vị trí")
    // public DriverTrackingStatusResDto getTrackingStatus() {
    // UUID driverId = UUID.fromString("68134fe6-287b-453f-a709-45719e4c35f1");
    // return driverMobileService.getTrackingStatus(driverId);
    // }

    @Tag(name = "Driver Mobile")
    @GetMapping("/profile")
    @Operation(summary = "Lấy thông tin cá nhân tài xế")
    public DriverProfileResDto getProfile() {
        UUID driverId = SecurityUtils.getCurrentUserId();
        return driverMobileService.getDriverProfile(driverId);
    }

    @Tag(name = "Driver Mobile")
    @GetMapping("/earnings")
    @Operation(summary = "Lấy thông tin thu nhập tài xế")
    public DriverEarningsResDto getEarnings() {
        UUID driverId = SecurityUtils.getCurrentUserId();
        return driverMobileService.getDriverEarnings(driverId);
    }

    @Tag(name = "Driver Mobile")
    @GetMapping("/vehicle/detail")
    @Operation(summary = "Lấy thông tin chi tiết phương tiện")
    public DriverVehicleDetailResDto getVehicleDetail() {
        UUID driverId = SecurityUtils.getCurrentUserId();
        return driverMobileService.getVehicleDetail(driverId);
    }

    @Tag(name = "Driver Mobile")
    @GetMapping("/documents")
    @Operation(summary = "Lấy danh sách giấy tờ của tài xế")
    public List<DriverDocumentResDto> getDocuments() {
        UUID driverId = SecurityUtils.getCurrentUserId();
        return driverMobileService.getDriverDocuments(driverId);
    }

    @Tag(name = "Driver Mobile")
    @PutMapping("/profile")
    @Operation(summary = "Cập nhật thông tin cá nhân tài xế")
    public ResponseEntity<Void> updateProfile(@RequestBody UpdateProfileReqDto dto) {
        UUID driverId = SecurityUtils.getCurrentUserId();
        driverMobileService.updateProfile(driverId, dto);
        return ResponseEntity.ok().build();
    }

    @Tag(name = "Driver Mobile")
    @PostMapping("/bookings/negotiate-price")
    @Operation(summary = "Đàm phán lại giá cuốc xe")
    public ResponseEntity<Void> negotiatePrice(@RequestBody NegotiatePriceReqDto dto) {
        UUID driverId = SecurityUtils.getCurrentUserId();
        driverMobileService.negotiatePrice(driverId, dto);
        return ResponseEntity.ok().build();
    }

    @Tag(name = "Driver Mobile")
    @PostMapping("/bookings/add-surcharge")
    @Operation(summary = "Thêm phụ phí phát sinh cho cuốc xe")
    public ResponseEntity<Void> addSurcharge(@RequestBody AddSurchargeReqDto dto) {
        UUID driverId = SecurityUtils.getCurrentUserId();
        driverMobileService.addSurcharge(driverId, dto);
        return ResponseEntity.ok().build();
    }

    @Tag(name = "Driver Mobile")
    @GetMapping("/calendar/work-days")
    @Operation(summary = "Lấy danh sách các ngày có nhiệm vụ trong tháng")
    public List<WorkDaysResDto> getWorkDays(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        UUID driverId = SecurityUtils.getCurrentUserId();
        return driverMobileService.getWorkDays(driverId, year, month);
    }
}
