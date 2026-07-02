package com.sonnh.bookingcar.service.impl;

import com.sonnh.bookingcar.data.domain.Tour;
import com.sonnh.bookingcar.data.repository.TourRepository;
import com.sonnh.bookingcar.data.specification.TourSpecification;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.admin.AdminTourResDto;
import com.sonnh.bookingcar.exception.ResourceNotFoundException;
import com.sonnh.bookingcar.mapper.TourMapper;
import com.sonnh.bookingcar.service.interfaces.TouristTourService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TouristTourServiceImpl implements TouristTourService {

    private final TourRepository tourRepository;
    private final TourMapper tourMapper;

    @Override
    @Transactional(readOnly = true)
    public PageImplResDto<AdminTourResDto> getActiveTours(Pageable pageable) {
        Page<Tour> page = tourRepository.findAll(TourSpecification.isActive(), pageable);
        return PageImplResDto.fromPage(page.map(tourMapper::toAdminTourResDto));
    }

    @Override
    @Transactional(readOnly = true)
    public AdminTourResDto getTourDetail(UUID id) {
        Tour tour = tourRepository.findById(id)
                .filter(t -> t.getAudit().getIsActive())
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found or inactive with id: " + id));
        return tourMapper.toAdminTourResDto(tour);
    }
}
