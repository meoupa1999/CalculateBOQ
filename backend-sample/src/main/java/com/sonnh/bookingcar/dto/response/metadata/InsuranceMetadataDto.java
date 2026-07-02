package com.sonnh.bookingcar.dto.response.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceMetadataDto {
    private String insuranceNumber;
    private String insuredName;
    private String plateNumber;
    private String chassisNumber;
    private String engineNumber;
    private String startDate;
    private String endDate;
    private String category;
}
