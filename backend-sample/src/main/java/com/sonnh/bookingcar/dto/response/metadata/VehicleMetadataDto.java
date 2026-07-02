package com.sonnh.bookingcar.dto.response.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleMetadataDto {
    private String brand;
    private String modelCode;
    private String engineNumber;
    private String chassisNumber;
    private String manufactureYear;
    private String manufactureCountry;
    private General general;
    private Specs specs;
    private Weight weight;
    private Technical technical;
    private Others others;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class General {
        private String serriNo;
        private String issuedDate;
        private String expiredDate;
        private String registrationNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Specs {
        private String brand;
        private String modelCode;
        private String vehicleType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Weight {
        private Integer seats;
        private String kerbMass;
        private String authorizedPayload;
        private String authorizedTotalMass;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Technical {
        private String fuelType;
        private String engineDisplacement;
        private String wheelFormula;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Others {
        private String note;
        private Boolean isCommercialUse;
    }
}
