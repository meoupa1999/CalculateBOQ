package com.sonnh.bookingcar.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDashboardResponse {
    private long total;
    private long warningCount;  // > 7 and <= 90 days
    private long criticalCount; // 0 to 7 days
    private long expiredCount;  // < 0 days
}
