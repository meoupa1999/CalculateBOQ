package com.sonnh.bookingcar.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueDataPointDto {
    private String label; // e.g., "2024-05-01" or "Tháng 05"
    private BigDecimal amount;
    private long count;
}
