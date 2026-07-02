package com.sonnh.bookingcar.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardSummaryResDto {
    private long total;
    private long done;
    private long waiting;
    private long processing;
}
