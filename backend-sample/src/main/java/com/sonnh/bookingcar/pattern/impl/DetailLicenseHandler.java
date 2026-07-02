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
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DetailLicenseHandler implements DetailMetadataHandler {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public DocumentType getType() {
        return DocumentType.LICENSE;
    }

    @Override
    public Object parseMetadata(Document document) {
        if (document.getMetadata() == null) return null;
        DocumentDetailResponseDto.LicenseDto dto = objectMapper.convertValue(document.getMetadata(), DocumentDetailResponseDto.LicenseDto.class);
        if (document.getImages() != null) {
            for (Image img : document.getImages()) {
                if (img.getImageType() == ImageType.LICENSE_FRONT) {
                    dto.setFrontImagePath(img.getFilePath());
                }
            }
        }
        return dto;
    }
}
