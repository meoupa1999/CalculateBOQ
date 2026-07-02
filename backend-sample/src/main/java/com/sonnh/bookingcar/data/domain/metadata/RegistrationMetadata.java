package com.sonnh.bookingcar.data.domain.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationMetadata {
    private GeneralInfo general;
    private VehicleSpecs specs;
    private TechnicalSpecs technical;
    private WeightPayload weight;
    private Others others;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeneralInfo {
        private String registrationNumber;
        private String vehicleInspectionNo;
        private String inspectionReportNo;
        private Boolean inspectionStampNotIssued;
        private String serriNo;
        private java.time.LocalDate issuedDate;
        private java.time.LocalDate expiredDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleSpecs {
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
    public static class TechnicalSpecs {
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
    public static class WeightPayload {
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
    public static class Others {
        private Boolean isCommercialUse;
        private Boolean isModified;
        private Boolean equippedWithTachograph;
        private String note;
    }
}
