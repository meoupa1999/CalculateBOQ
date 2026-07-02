package com.sonnh.bookingcar.pattern.impl;

import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import com.sonnh.bookingcar.pattern.interfaces.DocumentEntityMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DocumentEntityMapperFactory {
    private final Map<DocumentType, DocumentEntityMapper> mappers;

    public DocumentEntityMapperFactory(List<DocumentEntityMapper> mapperList) {
        this.mappers = mapperList.stream()
                .collect(Collectors.toMap(DocumentEntityMapper::getType, Function.identity()));
    }

    public DocumentEntityMapper getMapper(DocumentType type) {
        return mappers.get(type);
    }
}
