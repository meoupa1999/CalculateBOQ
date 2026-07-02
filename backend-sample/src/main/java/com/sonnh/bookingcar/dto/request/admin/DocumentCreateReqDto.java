package com.sonnh.bookingcar.dto.request.admin;

import com.sonnh.bookingcar.data.domain.enums.DocumentOwnerType;
import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentCreateReqDto {
    private DocumentOwnerType ownerType;
    private UUID ownerId;
    private DocumentType documentType;
    
    private String documentNumber;
    private LocalDate issuedDate;
    private LocalDate expiredDate;
    private String issuedPlace;

    // Metadata using existing nested classes
    private DriverCreateReqDto.IdCardReqDto idCardMetadata;
    private DriverCreateReqDto.LicenseReqDto licenseMetadata;
    private VehicleCreateReqDto.RegistrationReqDto registrationMetadata;
    private VehicleCreateReqDto.MandatoryInsuranceReqDto insuranceMetadata;
    private VehicleCreateReqDto.BadgeReqDto badgeMetadata;
}
