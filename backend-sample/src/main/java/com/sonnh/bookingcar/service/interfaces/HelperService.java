package com.sonnh.bookingcar.service.interfaces;

import com.sonnh.bookingcar.data.domain.Document;

public interface HelperService {
    <T> T parseMetadata(Document doc, Class<T> clazz);
}
