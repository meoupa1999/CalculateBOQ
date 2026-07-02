package com.sonnh.bookingcar.dto.response.driver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverSystemRequestResDto {
    private UUID id; // Shift ID
    private String title;
    private LocalTime scheduledStartTime;
    private LocalTime scheduledEndTime;
    private UUID shiftHistoryId; // Hidden field
}
