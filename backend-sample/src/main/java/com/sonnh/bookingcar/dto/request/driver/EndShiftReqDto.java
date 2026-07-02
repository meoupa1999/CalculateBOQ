package com.sonnh.bookingcar.dto.request.driver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndShiftReqDto {
    private UUID shiftId;
    private UUID shiftHistoryId;
    private BigDecimal endMileage;
    private Integer endFuelLevel;
    private String notes;
    private List<String> imageUrls;
}
