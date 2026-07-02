package com.sonnh.bookingcar.pattern.impl;

import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import com.sonnh.bookingcar.pattern.interfaces.DetailMetadataHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DetailMetadataHandlerFactory {
    private final Map<DocumentType, DetailMetadataHandler> map;

    public DetailMetadataHandlerFactory(List<DetailMetadataHandler> handlers) {
        this.map = handlers.stream()
                .collect(Collectors.toMap(
                        DetailMetadataHandler::getType,
                        Function.identity()));
    }

    public Object parseMetadata(DocumentType type, com.sonnh.bookingcar.data.domain.Document document) {
        if (document == null || type == null) return null;
        DetailMetadataHandler handler = map.get(type);
        return handler != null ? handler.parseMetadata(document) : document.getMetadata();
    }
}
