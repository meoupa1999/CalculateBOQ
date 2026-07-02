package com.sonnh.bookingcar.service.impl;

import com.sonnh.bookingcar.data.repository.CheckinTourHistoryRepository;
import com.sonnh.bookingcar.data.domain.CheckinTourHistory;
import com.sonnh.bookingcar.dto.response.admin.CheckinTourHistoryResDto;
import com.sonnh.bookingcar.exception.ResourceNotFoundException;
import com.sonnh.bookingcar.mapper.CheckinTourHistoryMapper;
import com.sonnh.bookingcar.service.interfaces.CheckinTourHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckinTourHistoryServiceImpl implements CheckinTourHistoryService {

    private final CheckinTourHistoryRepository repository;
    private final CheckinTourHistoryMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<CheckinTourHistoryResDto> getAll(UUID tourId) {
        List<CheckinTourHistory> histories;
        if (tourId != null) {
            histories = repository.findByTourId(tourId);
        } else {
            histories = repository.findAll();
        }
        return histories.stream()
                .map(mapper::toResDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CheckinTourHistoryResDto getById(UUID id) {
        return repository.findById(id)
                .map(mapper::toResDto)
                .orElseThrow(() -> new ResourceNotFoundException("Checkin history not found"));
    }
}
