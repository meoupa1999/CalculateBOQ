package com.sonnh.bookingcar.dto.response.driver;

import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import com.sonnh.bookingcar.data.domain.enums.ServiceType;
import com.sonnh.bookingcar.dto.response.admin.AdminTourResDto;
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
public class DriverTourDetailResDto {
    private UUID requestId;
    private String bookingCode;
    private ServiceType type;
    private BookingStatus status;
    private String customerName;
    private String customerPhone;
    private String notes;
    private String paymentMethod;
    private Double totalPrice;
    private java.time.LocalDateTime estimateEndTime;
    private TourDetailData tourDetail;

    private CancelReasonResDto cancelReason;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancelReasonResDto {
        private String reasonNote;
        private String role;
        private String userName;
    }

    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TourDetailData {
        private String pickupLocation;
        private LocalDate pickupDate;
        private LocalTime pickupTime;
        private Integer passengers;
        private AdminTourResDto tour;
    }
}
