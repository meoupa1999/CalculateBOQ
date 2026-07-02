package com.sonnh.bookingcar.dto.response.driver;

import com.sonnh.bookingcar.data.domain.enums.ShiftHistoryStatus;
import com.sonnh.bookingcar.data.domain.enums.ShiftStatus;
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
public class DriverShiftDetailResDto {
    private UUID id;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private ShiftStatus status;
    private ShiftHistoryResDto currentHistory;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShiftHistoryResDto {
        private UUID id;
        private LocalDate actualStartDate;
        private LocalTime actualStartTime;
        private LocalTime actualEndTime;
        private ShiftHistoryStatus finalStatus;
        private boolean isProcessing;
    }
}
