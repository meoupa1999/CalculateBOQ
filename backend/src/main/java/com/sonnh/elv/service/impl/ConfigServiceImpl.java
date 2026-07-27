package com.sonnh.elv.service.impl;

import com.sonnh.elv.data.domain.Config;
import com.sonnh.elv.data.domain.ProductType;
import com.sonnh.elv.data.repository.CategoryRepository;
import com.sonnh.elv.data.repository.ConfigRepository;
import com.sonnh.elv.data.repository.ProductTypeRepository;
import com.sonnh.elv.dto.request.BOMBatchRequestDTO;
import com.sonnh.elv.dto.request.UpdateConfigReqDto;
import com.sonnh.elv.data.domain.Category;
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
    private final CategoryRepository categoryRepository;
    private final ProductTypeRepository productTypeRepository;

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

    public void configBOM(BOMBatchRequestDTO dto) {
        // create
        dto.getCreate().forEach(createItemDTO -> create(createItemDTO));

        // update
        dto.getUpdate().forEach(updateItemDTO -> update(updateItemDTO));

        // delete
        dto.getDelete().forEach(id -> delete(id));
    }

    private void create(BOMBatchRequestDTO.CreateItemDTO dto) {
        ProductType productType = ProductType.builder()
                .name(dto.getName())
                .unit(dto.getUnit())
                .labor(dto.getLabor())
                .orderIndex(dto.getOrderIndex())
                .formula(dto.getFormula())
                .note(dto.getNote())
                .code(dto.getCode())
                .build();
        Category category = categoryRepository.findById(dto.getCategoryId()).orElseThrow();
        productType.addCategory(category);
        productTypeRepository.save(productType);

    }

    private void update(BOMBatchRequestDTO.UpdateItemDTO dto) {
        ProductType productType = productTypeRepository.findById(dto.getId()).orElseThrow();
        if (dto.getName() != null) {
            productType.setName(dto.getName());
        }
        if (dto.getUnit() != null) {
            productType.setUnit(dto.getUnit());
        }
        if (dto.getLabor() != null) {
            productType.setLabor(dto.getLabor());
        }
        if (dto.getOrderIndex() != null) {
            productType.setOrderIndex(dto.getOrderIndex());
        }
        if (dto.getFormula() != null) {
            productType.setFormula(dto.getFormula());
        }
        if (dto.getNote() != null) {
            productType.setNote(dto.getNote());
        }
        if (dto.getCode() != null) {
            productType.setCode(dto.getCode());
        }
        productTypeRepository.save(productType);
    }

    private void delete(UUID id) {
        ProductType productType = productTypeRepository.findById(id).orElseThrow();
        productType.getAudit().setIsActive(false);
        productTypeRepository.save(productType);
    }
}
