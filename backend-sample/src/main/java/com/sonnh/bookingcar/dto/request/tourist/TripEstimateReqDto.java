package com.sonnh.bookingcar.dto.request.tourist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripEstimateReqDto {
    private Double pickupLat;
    private Double pickupLon;
    private String pickupAddress;
    private Double destLat;
    private Double destLon;
    private String destAddress;
    private Double distance;
    private Double duration;
    private String vehicleType;
    private String paymentMethod;
}
