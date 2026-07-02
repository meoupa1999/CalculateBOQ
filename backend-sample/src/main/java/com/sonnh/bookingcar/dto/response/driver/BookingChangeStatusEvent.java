package com.sonnh.bookingcar.dto.response.driver;

import com.sonnh.bookingcar.dto.response.admin.AdminBookingResDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingChangeStatusEvent {
    private AdminBookingResDto bookingResponseDto;

}
