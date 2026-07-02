package com.sonnh.bookingcar.service.strategy.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonnh.bookingcar.dto.response.metadata.InsuranceMetadataDto;
import com.sonnh.bookingcar.service.strategy.DocumentMetadataMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("INSURANCEMetadataMapper")
public class InsuranceMetadataMapper implements DocumentMetadataMapper {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public Object map(Map<String, Object> rawData) {
        if (rawData == null) return null;
        return objectMapper.convertValue(rawData, InsuranceMetadataDto.class);
    }
}
