package com.sonnh.bookingcar.service.interfaces;

import com.sonnh.bookingcar.dto.request.admin.BookingDispatchReqDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.admin.AdminBookingResDto;
import com.sonnh.bookingcar.dto.response.admin.AdminAirportBookingResDto;
import com.sonnh.bookingcar.dto.response.admin.AdminDashboardSummaryResDto;
import com.sonnh.bookingcar.dto.response.admin.AdminTourBookingResDto;
import com.sonnh.bookingcar.dto.response.admin.MilestoneResDto;
import com.sonnh.bookingcar.dto.response.admin.ShiftResDto;
import com.sonnh.bookingcar.dto.response.tourist.TouristEstimateResDto;
import com.sonnh.bookingcar.dto.request.tourist.TripEstimateReqDto;
import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import com.sonnh.bookingcar.data.domain.enums.ServiceType;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface AdminBookingService {
    PageImplResDto<AdminBookingResDto> getBookings(Integer page, Integer size, String search, BookingStatus status,
            ServiceType type);

    AdminAirportBookingResDto getAirportBookingById(UUID id);

    AdminTourBookingResDto getTourBookingById(UUID id);

    List<ShiftResDto> getAvailableShiftsForBooking(UUID bookingId);

    AdminDashboardSummaryResDto getDashboardSummary();

    void dispatchBooking(UUID id, BookingDispatchReqDto dto);

    void cancelRequest(UUID id, String reasonNote);

    TouristEstimateResDto estimate(TripEstimateReqDto request);

    List<MilestoneResDto> getMilestones(UUID bookingId);

    void updateRqTourTime(UUID id, LocalTime time);
}
