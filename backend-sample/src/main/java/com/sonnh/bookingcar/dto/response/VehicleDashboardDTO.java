package com.sonnh.bookingcar.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDashboardDTO {
    private long totalVehicles;
    private long availableVehicles;
    private long busyVehicles;
    private long maintenanceVehicles;
}
