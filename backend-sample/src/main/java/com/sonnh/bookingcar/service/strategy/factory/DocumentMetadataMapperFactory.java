package com.sonnh.bookingcar.service.strategy.factory;

import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import com.sonnh.bookingcar.service.strategy.DocumentMetadataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DocumentMetadataMapperFactory {
    private final Map<String, DocumentMetadataMapper> mappers;

    public Object mapMetadata(DocumentType type, Map<String, Object> rawData) {
        if (rawData == null) return null;
        
        DocumentMetadataMapper mapper = resolveMapper(type);
        return mapper != null ? mapper.map(rawData) : rawData;
    }

    private DocumentMetadataMapper resolveMapper(DocumentType type) {
        switch (type) {
            case REGISTRATION:
            case BADGE:
                return mappers.get("REGISTRATIONMetadataMapper");
            case LICENSE:
            case ID_CARD:
                return mappers.get("LICENSEMetadataMapper");
            case MANDATORY_INSURANCE:
            case OTHER_INSURANCE:
                return mappers.get("INSURANCEMetadataMapper");
            default:
                return null;
        }
    }
}
