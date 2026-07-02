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
public class BadgeDtoMapper implements DocumentDtoMapper {
    private final ObjectMapper objectMapper;

    @Override
    public DocumentType getType() {
        return DocumentType.BADGE;
    }

    @Override
    public void map(Object resDto, Document doc) {
        VehicleResDto vehicleResDto = (VehicleResDto) resDto;
        VehicleResDto.BadgeDto metadataDto = objectMapper.convertValue(doc.getMetadata(), VehicleResDto.BadgeDto.class);
        final VehicleResDto.BadgeDto badgeDto = (metadataDto != null) ? metadataDto : new VehicleResDto.BadgeDto();


        badgeDto.setDocumentNumber(doc.getDocumentNumber());
        badgeDto.setIssuedDate(doc.getIssuedDate());
        badgeDto.setExpiredDate(doc.getExpiredDate());
        badgeDto.setId(doc.getId());

        doc.getImages().stream()
                .filter(img -> img.getImageType() == ImageType.BADGE_FRONT)
                .findFirst()
                .ifPresent(img -> badgeDto.setImageUrl(img.getFilePath()));

        vehicleResDto.setBadge(badgeDto);
    }
}
