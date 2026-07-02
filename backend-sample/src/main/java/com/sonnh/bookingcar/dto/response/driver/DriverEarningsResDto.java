package com.sonnh.bookingcar.dto.response.driver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverEarningsResDto {
    private BigDecimal totalEarnings;
    private Map<LocalDate, BigDecimal> dailyEarnings;
    private List<RecentTransaction> recentTransactions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentTransaction {
        private String title;
        private String sub;
        private BigDecimal amount;
        private String type; // "plus"
        private String status; // "DONE"
        private String date;
    }
}
