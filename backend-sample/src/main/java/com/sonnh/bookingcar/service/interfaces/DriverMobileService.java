package com.sonnh.bookingcar.service.interfaces;

import com.sonnh.bookingcar.dto.response.driver.DriverAirportDetailResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverRequestResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverShiftDetailResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverSystemRequestResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverTourDetailResDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.admin.ShiftResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverProfileResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverEarningsResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverVehicleDetailResDto;
import com.sonnh.bookingcar.dto.response.driver.WorkDaysResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverDocumentResDto;
import com.sonnh.bookingcar.dto.request.driver.UpdateProfileReqDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.sonnh.bookingcar.dto.request.driver.StartShiftReqDto;
import com.sonnh.bookingcar.dto.request.driver.EndShiftReqDto;
import com.sonnh.bookingcar.dto.request.driver.NegotiatePriceReqDto;
import com.sonnh.bookingcar.dto.request.driver.AddSurchargeReqDto;

public interface DriverMobileService {
    void startShift(StartShiftReqDto dto);

    void endShift(EndShiftReqDto dto);

    void confirmEarlyClosure(UUID shiftId, UUID historyId);

    PageImplResDto<DriverRequestResDto> getAssignedRequests(UUID driverId, LocalDateTime startDate,
            LocalDateTime endDate, Integer page, Integer size);

    List<DriverRequestResDto> getBookingHistory(UUID driverId);

    DriverAirportDetailResDto getAirportRequestDetail(UUID requestId);

    DriverTourDetailResDto getTourRequestDetail(UUID requestId);

    ShiftResDto getMyShifts(UUID driverId);

    List<DriverSystemRequestResDto> getSystemRequests(UUID driverId);

    DriverShiftDetailResDto getShiftDetail(UUID shiftId);

    // void updateLocation(DriverLocationReqDto dto);
    // List<DriverLocationResDto> getAllDriverLocation(double lat, double lng);
    // DriverLocationResDto getDriverLocation(UUID driverId);
    void startTracking(UUID driverId);

    void stopTracking(UUID driverId);

    void startGlobalTracking();

    void stopGlobalTracking();
    // DriverTrackingStatusResDto getTrackingStatus(UUID driverId);

    DriverProfileResDto getDriverProfile(UUID driverId);

    DriverEarningsResDto getDriverEarnings(UUID driverId);

    DriverVehicleDetailResDto getVehicleDetail(UUID driverId);

    List<DriverDocumentResDto> getDriverDocuments(UUID driverId);

    void updateProfile(UUID driverId, UpdateProfileReqDto dto);

    void negotiatePrice(UUID driverId, NegotiatePriceReqDto dto);

    void addSurcharge(UUID driverId, AddSurchargeReqDto dto);

    List<WorkDaysResDto> getWorkDays(UUID driverId, Integer year, Integer month);
}
