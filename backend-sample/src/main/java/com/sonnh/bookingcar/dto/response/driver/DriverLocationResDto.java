package com.sonnh.bookingcar.dto.response.driver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverLocationResDto {
    private UUID driverId;
    private Double latitude;
    private Double longitude;
}
