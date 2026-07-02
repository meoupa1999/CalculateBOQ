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
public class RegistrationDtoMapper implements DocumentDtoMapper {
    private final ObjectMapper objectMapper;

    @Override
    public DocumentType getType() {
        return DocumentType.REGISTRATION;
    }

    @Override
    public void map(Object resDto, Document doc) {
        VehicleResDto vehicleResDto = (VehicleResDto) resDto;
        VehicleResDto.RegistrationDto metadataDto = objectMapper.convertValue(doc.getMetadata(), VehicleResDto.RegistrationDto.class);
        final VehicleResDto.RegistrationDto registrationDto = (metadataDto != null) ? metadataDto : new VehicleResDto.RegistrationDto();

        
        VehicleResDto.RegistrationDto.GeneralInfoDto general = registrationDto.getGeneral();
        if (general == null) {
            general = new VehicleResDto.RegistrationDto.GeneralInfoDto();
        }
        
        general.setIssuedDate(doc.getIssuedDate());
        general.setExpiredDate(doc.getExpiredDate());
        
        registrationDto.setGeneral(general);
        registrationDto.setId(doc.getId());

        doc.getImages().stream()
                .filter(img -> img.getImageType() == ImageType.REGISTRATION_FRONT)
                .findFirst()
                .ifPresent(img -> registrationDto.setImageUrl(img.getFilePath()));
        
        vehicleResDto.setRegistration(registrationDto);
    }
}
