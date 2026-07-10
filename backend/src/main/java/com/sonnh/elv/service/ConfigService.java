package com.sonnh.elv.service;

import com.sonnh.elv.dto.request.UpdateConfigReqDto;
import com.sonnh.elv.dto.response.ConfigResponseDto;
import java.util.UUID;

public interface ConfigService {
    void updateConfig(UUID id, UpdateConfigReqDto dto);
    ConfigResponseDto getConfig(UUID id);
}
