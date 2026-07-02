package com.sonnh.bookingcar.service.interfaces;

import com.sonnh.bookingcar.dto.request.driver.DriverCheckinReqDto;
import com.sonnh.bookingcar.dto.response.admin.AdminTourResDto;
import java.util.List;
import java.util.UUID;

public interface DriverCheckinService {
    void checkin(UUID driverId, DriverCheckinReqDto dto);
    List<AdminTourResDto.SpecialLocationMappingResDto> getSpecialLocationTours(UUID tourId);
}
