package com.sonnh.bookingcar.dto.request.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUpdateReqDto {
    private String documentNumber;
    private LocalDate issuedDate;
    private LocalDate expiredDate;
    private String issuedPlace;

    // Metadata components
    private DriverUpdateReqDto.IdCardReqDto idCardMetadata;
    private DriverUpdateReqDto.LicenseReqDto licenseMetadata;
    private VehicleUpdateReqDto.RegistrationReqDto registrationMetadata;
    private VehicleUpdateReqDto.MandatoryInsuranceReqDto insuranceMetadata;
    private VehicleUpdateReqDto.BadgeReqDto badgeMetadata;
}
