package com.sonnh.bookingcar.service.interfaces;

import com.sonnh.bookingcar.data.domain.Shift;
import com.sonnh.bookingcar.data.domain.enums.ShiftHistoryStatus;
import com.sonnh.bookingcar.dto.request.admin.ShiftCreateReqDto;
import com.sonnh.bookingcar.dto.request.admin.ShiftUpdateReqDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.admin.ShiftResDto;

import java.util.UUID;

public interface ShiftService {
    PageImplResDto<ShiftResDto> getAllShifts(Integer page, Integer size);

    ShiftResDto getShiftById(UUID shiftId);

    ShiftResDto createShift(ShiftCreateReqDto dto);

    ShiftResDto updateShift(UUID shiftId, ShiftUpdateReqDto dto);

    void requestEarlyClosure(UUID shiftId);

    boolean hasActiveBookings(Shift shift);

    void finalizeShift(Shift shift, ShiftHistoryStatus finalStatus);
}
