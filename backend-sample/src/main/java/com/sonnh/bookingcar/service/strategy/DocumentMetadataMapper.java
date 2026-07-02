package com.sonnh.bookingcar.service.strategy;

import java.util.Map;

public interface DocumentMetadataMapper {
    Object map(Map<String, Object> rawData);
}
