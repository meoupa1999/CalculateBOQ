package com.sonnh.elv.service.impl;

import com.sonnh.elv.data.domain.Config;
import com.sonnh.elv.data.repository.ConfigRepository;
import com.sonnh.elv.dto.request.UpdateConfigReqDto;
import com.sonnh.elv.dto.response.ConfigResponseDto;
import com.sonnh.elv.service.ConfigService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ConfigRepository configRepository;

    @Override
    @Transactional
    public void updateConfig(UUID id, UpdateConfigReqDto dto) {
        Config config = configRepository.findById(id).orElseThrow();
        
        if (dto.getConditionLength() != null) {
            config.setConditionLength(dto.getConditionLength());
        }
        if (dto.getSw24ConditionQuanity() != null) {
            config.setSw24ConditionQuanity(dto.getSw24ConditionQuanity());
        }
        if (dto.getSw16ConditionQuanity() != null) {
            config.setSw16ConditionQuanity(dto.getSw16ConditionQuanity());
        }
        if (dto.getUps() != null) {
            config.setUps(dto.getUps());
        }
        if (dto.getPdu() != null) {
            config.setPdu(dto.getPdu());
        }
        if (dto.getConverter() != null) {
            config.setConverter(dto.getConverter());
        }
        
        configRepository.save(config);
    }

    @Override
    public ConfigResponseDto getConfig(UUID id) {
        Config config = configRepository.findById(id).orElseThrow();
        return ConfigResponseDto.builder()
                .id(config.getId())
                .conditionLength(config.getConditionLength())
                .sw24ConditionQuanity(config.getSw24ConditionQuanity())
                .sw16ConditionQuanity(config.getSw16ConditionQuanity())
                .ups(config.getUps())
                .pdu(config.getPdu())
                .converter(config.getConverter())
                .build();
    }
}
