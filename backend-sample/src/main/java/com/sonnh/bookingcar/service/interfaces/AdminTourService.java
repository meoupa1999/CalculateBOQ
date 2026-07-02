package com.sonnh.bookingcar.service.interfaces;

import com.sonnh.bookingcar.dto.request.admin.AdminTourReqDto;
import com.sonnh.bookingcar.dto.request.admin.AdminTourUpdateReqDto;
import com.sonnh.bookingcar.dto.request.admin.TourPriceUpdateDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.admin.AdminTourResDto;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface AdminTourService {
    AdminTourResDto createTour(AdminTourReqDto dto);
    PageImplResDto<AdminTourResDto> getTours(String search, Pageable pageable);
    AdminTourResDto getTourById(UUID id);

    AdminTourResDto updateTour(UUID id, AdminTourUpdateReqDto dto);

    void bulkUpdatePrices(List<TourPriceUpdateDto> dtos);

    void deleteTour(UUID id);
}
