package com.sonnh.bookingcar.dto.request.driver;

import lombok.Data;
import java.util.UUID;

@Data
public class DriverLocationReqDto {
    private UUID driverId;
    private Double latitude;
    private Double longitude;
}
