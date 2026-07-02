package com.sonnh.bookingcar.pattern.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonnh.bookingcar.data.domain.Document;
import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import com.sonnh.bookingcar.data.domain.enums.ImageType;
import com.sonnh.bookingcar.dto.response.admin.DriverResDto;
import com.sonnh.bookingcar.pattern.interfaces.DocumentDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdCardDtoMapper implements DocumentDtoMapper {
    private final ObjectMapper objectMapper;

    @Override
    public DocumentType getType() {
        return DocumentType.ID_CARD;
    }

    @Override
    public void map(Object resDto, Document doc) {
        DriverResDto driverResDto = (DriverResDto) resDto;
        DriverResDto.IdCardDto metadataDto = objectMapper.convertValue(doc.getMetadata(), DriverResDto.IdCardDto.class);
        final DriverResDto.IdCardDto idCardDto = (metadataDto != null) ? metadataDto : new DriverResDto.IdCardDto();

        DriverResDto.DocumentDto documentDto = driverResDto.getDocumentDto();
        if (documentDto == null) {
            documentDto = new DriverResDto.DocumentDto();
            driverResDto.setDocumentDto(documentDto);
        }
        
        idCardDto.setDocumentNumber(doc.getDocumentNumber());
        idCardDto.setIssuedDate(doc.getIssuedDate());
        idCardDto.setExpiredDate(doc.getExpiredDate());
        idCardDto.setIssuedPlace(doc.getIssuedPlace());
        idCardDto.setOwnerType(doc.getOwnerType());
        idCardDto.setDocumentType(doc.getDocumentType());
        idCardDto.setId(doc.getId());

        // Map Document Images
        doc.getImages().stream()
                .filter(img -> img.getImageType() == ImageType.ID_CARD_FRONT)
                .findFirst()
                .ifPresent(img -> idCardDto.setFrontImagePath(img.getFilePath()));
        doc.getImages().stream()
                .filter(img -> img.getImageType() == ImageType.ID_CARD_BACK)
                .findFirst()
                .ifPresent(img -> idCardDto.setBackImagePath(img.getFilePath()));

        documentDto.setIdCardDto(idCardDto);
    }
}
