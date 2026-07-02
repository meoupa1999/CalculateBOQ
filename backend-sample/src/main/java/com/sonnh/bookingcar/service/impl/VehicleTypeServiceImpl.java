package com.sonnh.bookingcar.service.impl;

import com.sonnh.bookingcar.data.domain.VehicleType;
import com.sonnh.bookingcar.data.repository.VehicleTypeRepository;
import com.sonnh.bookingcar.data.specification.VehicleTypeSpecification;
import com.sonnh.bookingcar.dto.request.admin.VehicleTypeReqDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.admin.VehicleTypeResDto;
import com.sonnh.bookingcar.exception.ResourceNotFoundException;
import com.sonnh.bookingcar.mapper.VehicleTypeMapper;
import com.sonnh.bookingcar.service.interfaces.VehicleTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sonnh.bookingcar.dto.response.admin.VehicleCategoryResDto;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleTypeServiceImpl implements VehicleTypeService {
    private final VehicleTypeRepository vehicleTypeRepository;
    private final VehicleTypeMapper vehicleTypeMapper;

    @Override
    @Transactional
    public VehicleTypeResDto create(VehicleTypeReqDto dto) {
        VehicleType entity = vehicleTypeMapper.toEntity(dto);
        entity = vehicleTypeRepository.save(entity);
        return vehicleTypeMapper.toResDto(entity);
    }

    @Override
    @Transactional
    public VehicleTypeResDto update(UUID id, VehicleTypeReqDto dto) {
        VehicleType entity = vehicleTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle type not found"));
        vehicleTypeMapper.updateEntityFromDto(dto, entity);
        entity = vehicleTypeRepository.save(entity);
        return vehicleTypeMapper.toResDto(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        VehicleType entity = vehicleTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle type not found"));
        entity.getAudit().setIsActive(false);
        vehicleTypeRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleTypeResDto getById(UUID id) {
        VehicleType entity = vehicleTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle type not found"));
        return vehicleTypeMapper.toResDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageImplResDto<VehicleTypeResDto> getAll(String search, Integer page, Integer size) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page != null && page > 0 ? page - 1 : 0,
                size != null && size > 0 ? size : 10,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                        "audit.updatedAt"));

        Specification<VehicleType> spec = Specification.where(VehicleTypeSpecification.isActive())
                .and(VehicleTypeSpecification.search(search));
        Page<VehicleType> pageResult = vehicleTypeRepository.findAll(spec, pageable);
        return PageImplResDto.fromPage(pageResult.map(vehicleTypeMapper::toResDto));
    }

    @Override
    public List<VehicleCategoryResDto> getAllCategories() {
        return vehicleTypeRepository.findAll().stream()
                .filter(vt -> vt.getAudit().getIsActive())
                // .filter(vt -> !vt.getIsDistanceBookingEnabled())
                .map(vt -> VehicleCategoryResDto.builder()
                        .id(vt.getId())
                        .name(vt.getName())
                        .build())
                .collect(Collectors.toList());
    }
}
