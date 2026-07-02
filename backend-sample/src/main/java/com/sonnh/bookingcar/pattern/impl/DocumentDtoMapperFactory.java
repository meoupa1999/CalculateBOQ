package com.sonnh.bookingcar.pattern.impl;

import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import com.sonnh.bookingcar.pattern.interfaces.DocumentDtoMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DocumentDtoMapperFactory {
    private final Map<DocumentType, DocumentDtoMapper> mappers;

    public DocumentDtoMapperFactory(List<DocumentDtoMapper> mapperList) {
        this.mappers = mapperList.stream()
                .collect(Collectors.toMap(DocumentDtoMapper::getType, Function.identity()));
    }

    public DocumentDtoMapper getMapper(DocumentType type) {
        return mappers.get(type);
    }
}
