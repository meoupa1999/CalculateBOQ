package com.sonnh.bookingcar.service.interfaces;

import com.sonnh.bookingcar.dto.request.admin.AdminSpecialLocationReqDto;
import com.sonnh.bookingcar.dto.response.admin.AdminSpecialLocationResDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AdminSpecialLocationService {
    AdminSpecialLocationResDto create(AdminSpecialLocationReqDto dto);
    AdminSpecialLocationResDto update(UUID id, AdminSpecialLocationReqDto dto);
    void delete(UUID id);
    AdminSpecialLocationResDto getDetail(UUID id);
    PageImplResDto<AdminSpecialLocationResDto> getAll(String searchName, Pageable pageable);
}
