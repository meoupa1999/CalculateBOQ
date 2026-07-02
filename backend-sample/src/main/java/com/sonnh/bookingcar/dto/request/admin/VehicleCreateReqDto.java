package com.sonnh.bookingcar.dto.request.admin;


import com.sonnh.bookingcar.data.domain.enums.VehicleStatus;
import com.sonnh.bookingcar.data.domain.enums.VehicleOwnership;
import java.time.LocalDate;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleCreateReqDto {
    private String model;
    private String plateNumber;
    private String type;
    private java.util.UUID vehicleType;
    private VehicleStatus status;
    private String year;
    private String color;
    private VehicleOwnership ownershipType;
    
    private MandatoryInsuranceReqDto mandatoryInsurance;
    private RegistrationReqDto registration;
    private BadgeReqDto badge;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MandatoryInsuranceReqDto {
        private String documentNumber;
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
    public static class RegistrationReqDto {
        private String registrationNumber;
        private String vehicleInspectionNo;
        private String vehicleType;
        private String brand;
        private String modelCode;
        private String engineNumber;
        private String chassisNumber;
        private String manufactureYear;
        private String manufactureCountry;
        private String lifetimeLimit;
        private Boolean isCommercialUse;
        private Boolean isModified;
        private String wheelFormula;
        private String wheelTread;
        private String overallDimension;
        private String largestLuggageDimension;
        private String wheelbase;
        private String kerbMass;
        private String authorizedPayload;
        private String authorizedTotalMass;
        private String authorizedTowedMass;
        private Integer seats;
        private Integer stoodPlace;
        private Integer layingPlace;
        private String fuelType;
        private String engineDisplacement;
        private String maxOutputRpm;
        private String serriNo;
        private String tireSpecs;
        private String inspectionReportNo;
        private Boolean equippedWithTachograph;
        private Boolean inspectionStampNotIssued;
        private String note;
        private LocalDate issuedDate;
        private LocalDate expiredDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BadgeReqDto {
        private String documentNumber;
        private LocalDate issuedDate;
        private LocalDate expiredDate;
        private String agency;
    }
}
