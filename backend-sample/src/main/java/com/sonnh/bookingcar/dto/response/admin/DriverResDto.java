package com.sonnh.bookingcar.dto.response.admin;

import com.sonnh.bookingcar.data.domain.enums.DriverStatus;
import com.sonnh.bookingcar.data.domain.enums.DocumentOwnerType;
import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverResDto {
    private UUID id;
    private String fullName;
    private String username;
    private String email;
    private String phone;
    private Double driverRating;
    private DriverStatus driverStatus;
    private String profileImage;
    private DocumentDto documentDto;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentDto {
        private IdCardDto idCardDto;
        private LicenseDto licenseDto;
    }

    @Data
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdCardDto {
        private UUID id;
        private String documentNumber;
        private LocalDate issuedDate;
        private LocalDate expiredDate;
        private String issuedPlace;
        private DocumentOwnerType ownerType;
        private DocumentType documentType;
        private LocalDate dateOfBirth;
        private String sex;
        private String nationality;
        private String placeOfOrigin;
        private String placeOfResidence;
        private String personalIdentification;
        private String frontImagePath;
        private String backImagePath;
    }

    @Data
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LicenseDto {
        private UUID id;
        private String documentNumber;
        private LocalDate issuedDate;
        private LocalDate expiredDate;
        private String issuedPlace;
        private DocumentOwnerType ownerType;
        private DocumentType documentType;
        private String fullName;
        private LocalDate dateOfBirth;
        private String nationality;
        private String address;
        private String frontImagePath;
    }
}
