package com.sonnh.bookingcar.pattern.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import com.sonnh.bookingcar.data.domain.Document;
import com.sonnh.bookingcar.data.domain.Image;
import com.sonnh.bookingcar.data.domain.enums.ImageType;
import com.sonnh.bookingcar.dto.response.DocumentDetailResponseDto;
import com.sonnh.bookingcar.pattern.interfaces.DetailMetadataHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DetailIdCardHandler implements DetailMetadataHandler {
    private final ObjectMapper objectMapper;

    @Override
    public DocumentType getType() {
        return DocumentType.ID_CARD;
    }

    @Override
    public Object parseMetadata(Document document) {
        if (document.getMetadata() == null) return new DocumentDetailResponseDto.IdCardDto();
        
        DocumentDetailResponseDto.IdCardDto dto = objectMapper.convertValue(
                document.getMetadata(), 
                DocumentDetailResponseDto.IdCardDto.class
        );
        
        if (document.getImages() != null) {
            for (Image img : document.getImages()) {
                if (img.getImageType() == ImageType.ID_CARD_FRONT) {
                    dto.setFrontImagePath(img.getFilePath());
                } else if (img.getImageType() == ImageType.ID_CARD_BACK) {
                    dto.setBackImagePath(img.getFilePath());
                }
            }
        }
        return dto;
    }
}
