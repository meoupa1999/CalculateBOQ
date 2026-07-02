package com.sonnh.bookingcar.service.impl;

import com.sonnh.bookingcar.data.domain.*;
import com.sonnh.bookingcar.data.repository.*;
import com.sonnh.bookingcar.dto.request.driver.DriverCheckinReqDto;
import com.sonnh.bookingcar.exception.ResourceNotFoundException;
import com.sonnh.bookingcar.service.interfaces.DriverCheckinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import com.sonnh.bookingcar.dto.response.admin.AdminTourResDto;
import com.sonnh.bookingcar.mapper.TourMapper;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverCheckinServiceImpl implements DriverCheckinService {

    private final CheckinTourHistoryRepository checkinTourHistoryRepository;
    private final TourRepository tourRepository;
    private final UserRepository userRepository;
    private final SpecialLocationTourRepository specialLocationTourRepository;
    private final TourMapper tourMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AdminTourResDto.SpecialLocationMappingResDto> getSpecialLocationTours(UUID tourId) {
        return specialLocationTourRepository.findByTourIdOrderByPriorityAsc(tourId).stream()
                .map(tourMapper::toMappingResDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void checkin(UUID driverId, DriverCheckinReqDto dto) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

        Tour tour = tourRepository.findById(dto.getTourId())
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found"));

        SpecialLocationTour mapping = specialLocationTourRepository.findById(dto.getSpecialLocationTourId())
                .orElseThrow(() -> new ResourceNotFoundException("Special Location mapping not found"));

        CheckinTourHistory history = CheckinTourHistory.builder()
                .status(dto.getStatus())
                .reasonNote(dto.getReasonNote())
                .build();

        // Use helper methods for associations (from history side)
        history.addUser(driver);
        history.addTour(tour);
        history.addSpecialLocationTour(mapping);

        checkinTourHistoryRepository.save(history);
    }
}
