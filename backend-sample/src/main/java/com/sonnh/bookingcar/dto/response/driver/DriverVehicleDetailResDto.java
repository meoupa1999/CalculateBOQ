package com.sonnh.bookingcar.dto.response.driver;


import com.sonnh.bookingcar.data.domain.enums.VehicleOwnership;
import com.sonnh.bookingcar.data.domain.enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverVehicleDetailResDto {
    private UUID id;
    private String model;
    private String plateNumber;
    private java.util.UUID vehicleType;
    private String type;
    private VehicleStatus status;
    private String year;
    private String color;
    private VehicleOwnership ownershipType;
}
