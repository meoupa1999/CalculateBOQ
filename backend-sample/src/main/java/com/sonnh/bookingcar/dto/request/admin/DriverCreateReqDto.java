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
public class DriverCreateReqDto {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;

    private IdCardReqDto idCard;
    private LicenseReqDto license;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdCardReqDto {
        private String documentNumber;
        private LocalDate issuedDate;
        private LocalDate expiredDate;
        private String issuedPlace;
        private LocalDate dateOfBirth;
        private String sex;
        private String nationality;
        private String placeOfOrigin;
        private String placeOfResidence;
        private String personalIdentification;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LicenseReqDto {
        private String documentNumber;
        private LocalDate issuedDate;
        private LocalDate expiredDate;
        private String issuedPlace;
        private String fullName;
        private LocalDate dateOfBirth;
        private String nationality;
        private String address;
    }
}
