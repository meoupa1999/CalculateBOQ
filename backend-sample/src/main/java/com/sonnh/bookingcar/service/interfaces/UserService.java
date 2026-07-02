package com.sonnh.bookingcar.service.interfaces;

import com.sonnh.bookingcar.data.domain.enums.DriverStatus;
import com.sonnh.bookingcar.dto.request.admin.DriverCreateReqDto;
import com.sonnh.bookingcar.dto.request.admin.DriverUpdateReqDto;
import com.sonnh.bookingcar.dto.request.driver.DriverRegisterReqDto;
import com.sonnh.bookingcar.dto.request.tourist.TouristRegisterReqDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.admin.DriverResDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UserService {
    DriverResDto getDriverById(UUID id);
    PageImplResDto<DriverResDto> getAllDrivers(Integer page, Integer size, String search);
    PageImplResDto<DriverResDto> getDriverRegistrations(Integer page, Integer size, String search);
    void approveDriver(UUID id);
    void rejectDriver(UUID id, String reason);
    DriverResDto createDriver(DriverCreateReqDto dto, MultipartFile driverImage, MultipartFile idCardFront, MultipartFile idCardBack, MultipartFile licenseImage);
    DriverResDto driverRegister(DriverRegisterReqDto dto, MultipartFile driverImage, MultipartFile idCardFront, MultipartFile idCardBack, MultipartFile licenseImage);
    DriverResDto updateDriver(UUID id, DriverUpdateReqDto dto, MultipartFile driverImage, MultipartFile idCardFront,
            MultipartFile idCardBack, MultipartFile licenseImage);
    void deleteDriver(UUID id);

    java.util.List<com.sonnh.bookingcar.dto.UserTouristDto> findAllTourists();
    com.sonnh.bookingcar.dto.UserTouristDto findTouristById(UUID id);
    void deleteTourist(UUID id);
    void touristRegister(TouristRegisterReqDto dto);
}
