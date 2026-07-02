package com.sonnh.bookingcar.service.impl;

import com.sonnh.bookingcar.data.domain.SpecialLocation;
import com.sonnh.bookingcar.data.repository.SpecialLocationRepository;
import com.sonnh.bookingcar.data.specification.SpecialLocationSpecification;
import com.sonnh.bookingcar.dto.request.admin.AdminSpecialLocationReqDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.admin.AdminSpecialLocationResDto;
import com.sonnh.bookingcar.exception.ResourceNotFoundException;
import com.sonnh.bookingcar.mapper.SpecialLocationMapper;
import com.sonnh.bookingcar.service.interfaces.AdminSpecialLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminSpecialLocationServiceImpl implements AdminSpecialLocationService {

    private final SpecialLocationRepository repository;
    private final SpecialLocationMapper mapper;

    @Override
    @Transactional
    public AdminSpecialLocationResDto create(AdminSpecialLocationReqDto dto) {
        SpecialLocation entity = mapper.toEntity(dto);
        entity = repository.save(entity);
        return mapper.toResDto(entity);
    }

    @Override
    @Transactional
    public AdminSpecialLocationResDto update(UUID id, AdminSpecialLocationReqDto dto) {
        SpecialLocation entity = repository.findById(id)
                .filter(e -> e.getAudit().getIsActive())
                .orElseThrow(() -> new ResourceNotFoundException("Special location not found or inactive with id: " + id));
        
        mapper.updateEntityFromDto(dto, entity);
        entity = repository.save(entity);
        return mapper.toResDto(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        SpecialLocation entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Special location not found with id: " + id));
        
        entity.getAudit().setIsActive(false);
        repository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminSpecialLocationResDto getDetail(UUID id) {
        SpecialLocation entity = repository.findById(id)
                .filter(e -> e.getAudit().getIsActive())
                .orElseThrow(() -> new ResourceNotFoundException("Special location not found or inactive with id: " + id));
        
        return mapper.toResDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageImplResDto<AdminSpecialLocationResDto> getAll(String searchName, Pageable pageable) {
        org.springframework.data.jpa.domain.Specification<SpecialLocation> spec = SpecialLocationSpecification.isActive();
        if (searchName != null && !searchName.isEmpty()) {
            spec = spec.and(SpecialLocationSpecification.hasName(searchName));
        }
        Page<SpecialLocation> page = repository.findAll(spec, pageable);
        return PageImplResDto.fromPage(page.map(mapper::toResDto));
    }
}
