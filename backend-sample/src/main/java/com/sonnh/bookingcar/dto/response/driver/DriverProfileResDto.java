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
public class DriverProfileResDto {
    private UUID id;
    private String fullName;
    private String phone;
    private String email;
    private String profileImage;
    private Double rating;
    private String vehicleModel;
    private String plateNumber;
}
