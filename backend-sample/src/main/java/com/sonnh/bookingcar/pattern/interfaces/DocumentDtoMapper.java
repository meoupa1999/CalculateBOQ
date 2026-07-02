package com.sonnh.bookingcar.pattern.interfaces;

import com.sonnh.bookingcar.data.domain.Document;
import com.sonnh.bookingcar.data.domain.enums.DocumentType;

public interface DocumentDtoMapper {
    DocumentType getType();
    void map(Object resDto, Document doc);
}
