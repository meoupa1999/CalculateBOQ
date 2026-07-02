package com.sonnh.bookingcar.dto.response.tourist;

import com.sonnh.bookingcar.dto.response.admin.AdminBookingResDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreatedEvent {
    private AdminBookingResDto bookingRequesDto;

}
