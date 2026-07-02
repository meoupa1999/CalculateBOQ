package com.sonnh.bookingcar.dto.response.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverMetadataDto {
    private String fullName;
    private String dob;
    private String nationality;
    private String address;
    private String gender;
    private String licenseClass;
    private String issuedDate;
    private String expiredDate;
}
