package com.sonnh.bookingcar.dto.response.admin;

import com.sonnh.bookingcar.data.domain.enums.VehicleStatus;
import com.sonnh.bookingcar.data.domain.enums.VehicleOwnership;
import java.time.LocalDate;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResDto {
    private UUID id;
    private String plateNumber;
    private String model;
    private VehicleStatus status; // AVAILABLE, MAINTENANCE, BUSY
    private VehicleTypeDto vehicleType;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleTypeDto {
        private UUID id;
        private String name;
    }

    private String year;
    private String color;
    private VehicleOwnership ownershipType;
    private String vehicleImage;

    private MandatoryInsuranceDto mandatoryInsurance;
    private RegistrationDto registration;
    private BadgeDto badge;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegistrationDto {
        private UUID id;
        private GeneralInfoDto general;
        private VehicleSpecsDto specs;
        private TechnicalSpecsDto technical;
        private WeightPayloadDto weight;
        private OthersDto others;
        private String imageUrl; // Keep image at top level for easy access

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
            private LocalDate issuedDate;
            private LocalDate expiredDate;
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
        private UUID id;
        private String documentNumber;
        private String imageUrl;
        private LocalDate issuedDate;
        private LocalDate expiredDate;
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
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BadgeDto {
        private UUID id;
        private String documentNumber;
        private String imageUrl;
        private LocalDate issuedDate;
        private LocalDate expiredDate;
        private String agency;
    }
}
