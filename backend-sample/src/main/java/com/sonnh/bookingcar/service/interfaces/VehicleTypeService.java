package com.sonnh.bookingcar.service.interfaces;

import com.sonnh.bookingcar.dto.request.admin.VehicleTypeReqDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.admin.VehicleTypeResDto;
import com.sonnh.bookingcar.dto.response.admin.VehicleCategoryResDto;
import java.util.List;
import java.util.UUID;

public interface VehicleTypeService {
    VehicleTypeResDto create(VehicleTypeReqDto dto);

    VehicleTypeResDto update(UUID id, VehicleTypeReqDto dto);

    void delete(UUID id);

    VehicleTypeResDto getById(UUID id);

    PageImplResDto<VehicleTypeResDto> getAll(String search, Integer page, Integer size);

    List<VehicleCategoryResDto> getAllCategories();
}