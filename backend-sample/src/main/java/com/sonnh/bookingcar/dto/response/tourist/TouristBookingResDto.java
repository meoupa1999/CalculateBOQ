package com.sonnh.bookingcar.dto.response.tourist;

import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import com.sonnh.bookingcar.data.domain.enums.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TouristBookingResDto {
    private UUID id;
    private String bookingCode;
    private ServiceType type;
    private BookingStatus status;
    private String pickupLocation;
    private String dropoffLocation;
    private String pickupDate;
    private String pickupTime;
    private BigDecimal totalPrice;
    private String driverName;
    private String vehicleInfo;
}
