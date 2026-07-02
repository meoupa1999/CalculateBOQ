package com.sonnh.bookingcar.service.interfaces;

import com.sonnh.bookingcar.dto.request.admin.VehicleCreateReqDto;
import com.sonnh.bookingcar.dto.request.admin.VehicleUpdateReqDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.VehicleDashboardDTO;
import com.sonnh.bookingcar.dto.response.admin.VehicleResDto;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface VehicleService {
    PageImplResDto<VehicleResDto> getAllVehicles(Integer page, Integer size, String search, Boolean missingDocuments);
    VehicleResDto getById(UUID id);
    VehicleResDto create(VehicleCreateReqDto dto, MultipartFile insuranceImage, MultipartFile registrationImage, MultipartFile badgeImage, MultipartFile vehicleImage);
    VehicleResDto update(UUID id, VehicleUpdateReqDto dto, MultipartFile insuranceImage, MultipartFile registrationImage, MultipartFile badgeImage, MultipartFile vehicleImage);
    VehicleDashboardDTO getVehicleDashboard();
    void delete(UUID id);
}
