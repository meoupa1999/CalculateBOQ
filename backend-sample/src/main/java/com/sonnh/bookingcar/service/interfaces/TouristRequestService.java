package com.sonnh.bookingcar.service.interfaces;

import com.sonnh.bookingcar.dto.request.tourist.AirportTransferReqDto;
import com.sonnh.bookingcar.dto.request.tourist.TourBookingReqDto;

import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import com.sonnh.bookingcar.data.domain.enums.ServiceType;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.tourist.TouristBookingDetailResDto;
import com.sonnh.bookingcar.dto.response.tourist.TouristBookingResDto;
import com.sonnh.bookingcar.dto.response.tourist.TouristEstimateResDto;
import com.sonnh.bookingcar.dto.request.tourist.TripEstimateReqDto;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TouristRequestService {
    UUID createAirportTransfer(AirportTransferReqDto dto);
    UUID createTourBooking(TourBookingReqDto dto);
    void cancelBooking(UUID id, String reasonNote);
    PageImplResDto<TouristBookingResDto> getTouristBookings(String searchKeyword, BookingStatus status, ServiceType type, Integer page, Integer size);
    TouristBookingDetailResDto getBookingDetail(UUID id);
    TouristEstimateResDto estimate(TripEstimateReqDto request);
}
