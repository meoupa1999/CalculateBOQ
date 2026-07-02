package com.sonnh.bookingcar.dto.response.driver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDispatchedAssigned {
    private DriverRequestResDto assignedRequests;
}
