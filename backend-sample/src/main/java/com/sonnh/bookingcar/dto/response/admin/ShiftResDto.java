package com.sonnh.bookingcar.dto.response.admin;

import com.sonnh.bookingcar.data.domain.enums.ShiftStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftResDto {
    private UUID id;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String driverName;
    private UUID driverId;
    private String vehicleInfo;
    private UUID vehicleId;
    private ShiftStatus status;
    private String notes;
    private boolean started;
    private boolean vehicleBusy;
    private boolean conflicted;
    private String conflictDriverName;
    private String conflictDriverPhone;
    @Builder.Default
    private List<UUID> duplicateBookingIds = new ArrayList<>();
}
