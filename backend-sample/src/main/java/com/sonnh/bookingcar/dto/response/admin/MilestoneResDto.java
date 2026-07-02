package com.sonnh.bookingcar.dto.response.admin;

import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneResDto {
    private BookingStatus status;
    private Audit audit;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Audit {
        private LocalDateTime createdAt;
    }
}
