package com.sonnh.bookingcar.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDropdownResDto {
    private UUID id;
    private String plateNumber;
    private List<DocumentMinDto> documents;
}
