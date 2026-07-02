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
public class DetailBadgeHandler implements DetailMetadataHandler {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public DocumentType getType() {
        return DocumentType.BADGE;
    }

    @Override
    public Object parseMetadata(Document document) {
        if (document.getMetadata() == null) return null;
        DocumentDetailResponseDto.BadgeDto dto = objectMapper.convertValue(document.getMetadata(), DocumentDetailResponseDto.BadgeDto.class);
        if (document.getImages() != null) {
            for (Image img : document.getImages()) {
                if (img.getImageType() == ImageType.BADGE_FRONT) {
                    dto.setImageUrl(img.getFilePath());
                }
            }
        }
        return dto;
    }
}
