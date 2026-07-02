package com.sonnh.bookingcar.dto.request.driver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddSurchargeReqDto {
    private UUID requestId;
    private String description;
    private BigDecimal amount;
}
