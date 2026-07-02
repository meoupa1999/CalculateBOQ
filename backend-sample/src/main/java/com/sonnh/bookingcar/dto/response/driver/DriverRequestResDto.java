package com.sonnh.bookingcar.dto.response.driver;

import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import com.sonnh.bookingcar.data.domain.enums.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverRequestResDto {
    private UUID requestId;
    private String bookingCode;
    private ServiceType type;
    private BookingStatus status;
    private String customerName;
    private String customerPhone;
    private Double totalPrice;
    private java.time.LocalDateTime estimateEndTime;
    private AirportDetailDto airportDetail;
    private TourDetailDto tourDetail;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AirportDetailDto {
        private String shortPickupLocation;
        private String shortDropoffLocation;
        private LocalDate pickupDate;
        private LocalTime pickupTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TourDetailDto {
        private String pickupLocation;
        private LocalDate pickupDate;
    }
}
