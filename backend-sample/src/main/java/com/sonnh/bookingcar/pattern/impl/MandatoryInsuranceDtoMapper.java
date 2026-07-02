package com.sonnh.bookingcar.pattern.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonnh.bookingcar.data.domain.Document;
import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import com.sonnh.bookingcar.data.domain.enums.ImageType;
import com.sonnh.bookingcar.dto.response.admin.VehicleResDto;
import com.sonnh.bookingcar.pattern.interfaces.DocumentDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MandatoryInsuranceDtoMapper implements DocumentDtoMapper {
    private final ObjectMapper objectMapper;

    @Override
    public DocumentType getType() {
        return DocumentType.MANDATORY_INSURANCE;
    }

    @Override
    public void map(Object resDto, Document doc) {
        VehicleResDto vehicleResDto = (VehicleResDto) resDto;
        VehicleResDto.MandatoryInsuranceDto metadataDto = objectMapper.convertValue(doc.getMetadata(),
                VehicleResDto.MandatoryInsuranceDto.class);
        final VehicleResDto.MandatoryInsuranceDto insuranceDto = (metadataDto != null) ? metadataDto : new VehicleResDto.MandatoryInsuranceDto();


        insuranceDto.setDocumentNumber(doc.getDocumentNumber());
        insuranceDto.setIssuedDate(doc.getIssuedDate());
        insuranceDto.setExpiredDate(doc.getExpiredDate());
        insuranceDto.setId(doc.getId());

        doc.getImages().stream()
                .filter(img -> img.getImageType() == ImageType.MANDATORY_INSURANCE_FRONT)
                .findFirst()
                .ifPresent(img -> insuranceDto.setImageUrl(img.getFilePath()));

        vehicleResDto.setMandatoryInsurance(insuranceDto);
    }
}
