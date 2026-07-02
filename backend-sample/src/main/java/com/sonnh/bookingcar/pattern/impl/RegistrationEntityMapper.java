package com.sonnh.bookingcar.pattern.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonnh.bookingcar.data.domain.Document;
import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import com.sonnh.bookingcar.data.domain.metadata.RegistrationMetadata;
import com.sonnh.bookingcar.dto.request.admin.VehicleCreateReqDto;
import com.sonnh.bookingcar.dto.request.admin.VehicleUpdateReqDto;
import com.sonnh.bookingcar.pattern.interfaces.DocumentEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RegistrationEntityMapper implements DocumentEntityMapper {
    private final ObjectMapper objectMapper;

    @Override
    public DocumentType getType() {
        return DocumentType.REGISTRATION;
    }

    @Override
    public void mapToEntity(Document document, Object updateDto) {
        if (updateDto == null) return;

        RegistrationMetadata metadata;
        java.time.LocalDate issuedDate;
        java.time.LocalDate expiredDate;

        if (updateDto instanceof VehicleCreateReqDto.RegistrationReqDto) {
            VehicleCreateReqDto.RegistrationReqDto dto = (VehicleCreateReqDto.RegistrationReqDto) updateDto;
            issuedDate = dto.getIssuedDate();
            expiredDate = dto.getExpiredDate();
            metadata = mapToMetadata(dto);
        } else if (updateDto instanceof VehicleUpdateReqDto.RegistrationReqDto) {
            VehicleUpdateReqDto.RegistrationReqDto dto = (VehicleUpdateReqDto.RegistrationReqDto) updateDto;
            issuedDate = dto.getIssuedDate();
            expiredDate = dto.getExpiredDate();
            metadata = mapToMetadata(dto);
        } else {
            throw new IllegalArgumentException("Unsupported DTO type: " + updateDto.getClass().getName());
        }

        document.setIssuedDate(issuedDate);
        document.setExpiredDate(expiredDate);
        document.setMetadata(objectMapper.convertValue(metadata, Map.class));
    }

    private RegistrationMetadata mapToMetadata(VehicleCreateReqDto.RegistrationReqDto dto) {
        return RegistrationMetadata.builder()
                .general(RegistrationMetadata.GeneralInfo.builder()
                        .registrationNumber(dto.getRegistrationNumber())
                        .vehicleInspectionNo(dto.getVehicleInspectionNo())
                        .inspectionReportNo(dto.getInspectionReportNo())
                        .inspectionStampNotIssued(dto.getInspectionStampNotIssued())
                        .serriNo(dto.getSerriNo())
                        .issuedDate(dto.getIssuedDate())
                        .expiredDate(dto.getExpiredDate())
                        .build())
                .specs(RegistrationMetadata.VehicleSpecs.builder()
                        .vehicleType(dto.getVehicleType())
                        .brand(dto.getBrand())
                        .modelCode(dto.getModelCode())
                        .engineNumber(dto.getEngineNumber())
                        .chassisNumber(dto.getChassisNumber())
                        .manufactureYear(dto.getManufactureYear())
                        .manufactureCountry(dto.getManufactureCountry())
                        .lifetimeLimit(dto.getLifetimeLimit())
                        .build())
                .technical(RegistrationMetadata.TechnicalSpecs.builder()
                        .wheelFormula(dto.getWheelFormula())
                        .wheelTread(dto.getWheelTread())
                        .overallDimension(dto.getOverallDimension())
                        .largestLuggageDimension(dto.getLargestLuggageDimension())
                        .wheelbase(dto.getWheelbase())
                        .fuelType(dto.getFuelType())
                        .engineDisplacement(dto.getEngineDisplacement())
                        .maxOutputRpm(dto.getMaxOutputRpm())
                        .tireSpecs(dto.getTireSpecs())
                        .build())
                .weight(RegistrationMetadata.WeightPayload.builder()
                        .kerbMass(dto.getKerbMass())
                        .authorizedPayload(dto.getAuthorizedPayload())
                        .authorizedTotalMass(dto.getAuthorizedTotalMass())
                        .authorizedTowedMass(dto.getAuthorizedTowedMass())
                        .seats(dto.getSeats())
                        .stoodPlace(dto.getStoodPlace())
                        .layingPlace(dto.getLayingPlace())
                        .build())
                .others(RegistrationMetadata.Others.builder()
                        .isCommercialUse(dto.getIsCommercialUse())
                        .isModified(dto.getIsModified())
                        .equippedWithTachograph(dto.getEquippedWithTachograph())
                        .note(dto.getNote())
                        .build())
                .build();
    }

    private RegistrationMetadata mapToMetadata(VehicleUpdateReqDto.RegistrationReqDto dto) {
        return RegistrationMetadata.builder()
                .general(RegistrationMetadata.GeneralInfo.builder()
                        .registrationNumber(dto.getRegistrationNumber())
                        .vehicleInspectionNo(dto.getVehicleInspectionNo())
                        .inspectionReportNo(dto.getInspectionReportNo())
                        .inspectionStampNotIssued(dto.getInspectionStampNotIssued())
                        .serriNo(dto.getSerriNo())
                        .issuedDate(dto.getIssuedDate())
                        .expiredDate(dto.getExpiredDate())
                        .build())
                .specs(RegistrationMetadata.VehicleSpecs.builder()
                        .vehicleType(dto.getVehicleType())
                        .brand(dto.getBrand())
                        .modelCode(dto.getModelCode())
                        .engineNumber(dto.getEngineNumber())
                        .chassisNumber(dto.getChassisNumber())
                        .manufactureYear(dto.getManufactureYear())
                        .manufactureCountry(dto.getManufactureCountry())
                        .lifetimeLimit(dto.getLifetimeLimit())
                        .build())
                .technical(RegistrationMetadata.TechnicalSpecs.builder()
                        .wheelFormula(dto.getWheelFormula())
                        .wheelTread(dto.getWheelTread())
                        .overallDimension(dto.getOverallDimension())
                        .largestLuggageDimension(dto.getLargestLuggageDimension())
                        .wheelbase(dto.getWheelbase())
                        .fuelType(dto.getFuelType())
                        .engineDisplacement(dto.getEngineDisplacement())
                        .maxOutputRpm(dto.getMaxOutputRpm())
                        .tireSpecs(dto.getTireSpecs())
                        .build())
                .weight(RegistrationMetadata.WeightPayload.builder()
                        .kerbMass(dto.getKerbMass())
                        .authorizedPayload(dto.getAuthorizedPayload())
                        .authorizedTotalMass(dto.getAuthorizedTotalMass())
                        .authorizedTowedMass(dto.getAuthorizedTowedMass())
                        .seats(dto.getSeats())
                        .stoodPlace(dto.getStoodPlace())
                        .layingPlace(dto.getLayingPlace())
                        .build())
                .others(RegistrationMetadata.Others.builder()
                        .isCommercialUse(dto.getIsCommercialUse())
                        .isModified(dto.getIsModified())
                        .equippedWithTachograph(dto.getEquippedWithTachograph())
                        .note(dto.getNote())
                        .build())
                .build();
    }
}
