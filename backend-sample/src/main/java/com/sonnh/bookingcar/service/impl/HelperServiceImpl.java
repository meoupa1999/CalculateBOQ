package com.sonnh.bookingcar.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonnh.bookingcar.data.domain.Document;
import com.sonnh.bookingcar.service.interfaces.HelperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HelperServiceImpl implements HelperService {
    private final ObjectMapper objectMapper;

    @Override
    public <T> T parseMetadata(Document doc, Class<T> clazz) {
        try {
            return objectMapper.convertValue(doc.getMetadata(), clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
