package com.sonnh.bookingcar.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialLocationResDto {
    private UUID id;
    private String locationName;
    private Double latitude;
    private Double longitude;
}
