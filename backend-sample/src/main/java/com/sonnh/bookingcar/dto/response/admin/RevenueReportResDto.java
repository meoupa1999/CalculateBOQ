package com.sonnh.bookingcar.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReportResDto {
    private BigDecimal totalRevenue;
    private long totalBookings;
    private List<RevenueDataPointDto> revenueByPeriod;
    private List<RevenueDataPointDto> revenueByService; // e.g., "TOUR", "AIRPORT_TRANSFER"
    private List<RevenueDataPointDto> revenueByDriver;
}
