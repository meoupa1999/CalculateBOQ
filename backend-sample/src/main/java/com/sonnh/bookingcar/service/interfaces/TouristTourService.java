package com.sonnh.bookingcar.service.interfaces;

import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.admin.AdminTourResDto;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface TouristTourService {
    PageImplResDto<AdminTourResDto> getActiveTours(Pageable pageable);
    AdminTourResDto getTourDetail(UUID id);
}
