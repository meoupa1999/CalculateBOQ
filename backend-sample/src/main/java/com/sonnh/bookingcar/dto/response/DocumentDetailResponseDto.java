package com.sonnh.bookingcar.dto.response;

import com.sonnh.bookingcar.data.domain.enums.DocumentOwnerType;
import com.sonnh.bookingcar.data.domain.enums.DocumentStatus;
import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDetailResponseDto {
    private UUID id;
    private String documentNumber;
    private DocumentOwnerType ownerType;
    private DocumentType documentType;
    private LocalDate issuedDate;
    private LocalDate expiredDate;
    private String issuedPlace;
    private Object metadata;
    private DocumentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private DriverInfo driver;
    private VehicleInfo vehicle;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DriverInfo {
        private UUID id;
        private String fullName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleInfo {
        private UUID id;
        private String plateNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdCardDto {
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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LicenseDto {
        private String fullName;
        private LocalDate dateOfBirth;
        private String nationality;
        private String address;
        private String frontImagePath;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegistrationDto {
        private GeneralInfoDto general;
        private VehicleSpecsDto specs;
        private TechnicalSpecsDto technical;
        private WeightPayloadDto weight;
        private OthersDto others;
        private String imageUrl;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class GeneralInfoDto {
            private String registrationNumber;
            private String vehicleInspectionNo;
            private String inspectionReportNo;
            private Boolean inspectionStampNotIssued;
            private String serriNo;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class VehicleSpecsDto {
            private String vehicleType;
            private String brand;
            private String modelCode;
            private String engineNumber;
            private String chassisNumber;
            private String manufactureYear;
            private String manufactureCountry;
            private String lifetimeLimit;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TechnicalSpecsDto {
            private String wheelFormula;
            private String wheelTread;
            private String overallDimension;
            private String largestLuggageDimension;
            private String wheelbase;
            private String fuelType;
            private String engineDisplacement;
            private String maxOutputRpm;
            private String tireSpecs;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class WeightPayloadDto {
            private String kerbMass;
            private String authorizedPayload;
            private String authorizedTotalMass;
            private String authorizedTowedMass;
            private Integer seats;
            private Integer stoodPlace;
            private Integer layingPlace;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class OthersDto {
            private Boolean isCommercialUse;
            private Boolean isModified;
            private Boolean equippedWithTachograph;
            private String note;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MandatoryInsuranceDto {
        private String ownerName;
        private String address;
        private String phoneNumber;
        private String plateNumber;
        private String chassisNumber;
        private String engineNumber;
        private String vehicleType;
        private String payload;
        private String seats;
        private String usagePurpose;
        private BigDecimal insuranceFee;
        private BigDecimal insuranceFeeVAT;
        private String issuer;
        private String imageUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BadgeDto {
        private String agency;
        private String imageUrl;
    }
}
