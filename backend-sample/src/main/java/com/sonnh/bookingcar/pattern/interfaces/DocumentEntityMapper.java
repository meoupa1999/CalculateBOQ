package com.sonnh.bookingcar.pattern.interfaces;

import com.sonnh.bookingcar.data.domain.Document;
import com.sonnh.bookingcar.data.domain.enums.DocumentType;

public interface DocumentEntityMapper {
    DocumentType getType();

    void mapToEntity(Document document, Object updateDto);
}
