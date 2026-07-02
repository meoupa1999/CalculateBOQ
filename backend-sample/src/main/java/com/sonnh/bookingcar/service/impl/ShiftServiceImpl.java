package com.sonnh.bookingcar.service.impl;

import com.sonnh.bookingcar.data.domain.Shift;
import com.sonnh.bookingcar.data.domain.ShiftHistory;
import com.sonnh.bookingcar.data.domain.User;
import com.sonnh.bookingcar.data.domain.Vehicle;
import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import com.sonnh.bookingcar.data.domain.enums.ShiftHistoryStatus;
import com.sonnh.bookingcar.data.domain.enums.ShiftStatus;
import com.sonnh.bookingcar.data.domain.enums.VehicleStatus;
import com.sonnh.bookingcar.data.repository.ShiftHistoryRepository;
import com.sonnh.bookingcar.data.repository.ShiftRepository;
import com.sonnh.bookingcar.data.repository.UserRepository;
import com.sonnh.bookingcar.data.repository.VehicleRepository;
import com.sonnh.bookingcar.data.specification.ShiftSpecification;
import com.sonnh.bookingcar.dto.request.admin.ShiftCreateReqDto;
import com.sonnh.bookingcar.dto.request.admin.ShiftUpdateReqDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.admin.ShiftResDto;
import com.sonnh.bookingcar.exception.ResourceNotFoundException;
import com.sonnh.bookingcar.mapper.ShiftMapper;
import com.sonnh.bookingcar.service.interfaces.ShiftService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ShiftHistoryRepository shiftHistoryRepository;
    private final ShiftMapper shiftMapper;

    public ShiftServiceImpl(ShiftRepository shiftRepository, UserRepository userRepository,
            VehicleRepository vehicleRepository, ShiftHistoryRepository shiftHistoryRepository,
            ShiftMapper shiftMapper) {
        this.shiftRepository = shiftRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.shiftHistoryRepository = shiftHistoryRepository;
        this.shiftMapper = shiftMapper;
    }

    @Override
    public PageImplResDto<ShiftResDto> getAllShifts(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "audit.updatedAt"));
        Page<Shift> shiftPage = shiftRepository.findAll(ShiftSpecification.isActive(), pageable);

        List<ShiftResDto> shiftResDtos = shiftPage.getContent().stream()
                .map(shiftMapper::toShiftResDto)
                .collect(Collectors.toList());

        return PageImplResDto.<ShiftResDto>builder()
                .content(shiftResDtos)
                .pageNumber(shiftPage.getNumber() + 1)
                .pageSize(shiftPage.getSize())
                .totalElements(shiftPage.getTotalElements())
                .totalPages(shiftPage.getTotalPages())
                .last(shiftPage.isLast())
                .build();
    }

    @Override
    public ShiftResDto getShiftById(UUID shiftId) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with id: " + shiftId));
        return shiftMapper.toShiftResDto(shift);
    }

    @Override
    public ShiftResDto createShift(ShiftCreateReqDto dto) {
        User driver = userRepository.findById(dto.getDriverId())
                .filter(u -> u.getAudit().getIsActive())
                .filter(u -> u.getRole().getName().equals("DRIVER"))
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found or inactive"));

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .filter(v -> v.getAudit().getIsActive())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found or inactive"));

        Shift shift = shiftMapper.toShift(dto);
        shift.addDriver(driver);
        shift.addVehicle(vehicle);

        // Note: For a real app, we would add validation here to check if the driver or
        // vehicle
        // is already booked during this time frame to prevent conflicts.

        Shift savedShift = shiftRepository.save(shift);
        return shiftMapper.toShiftResDto(savedShift);
    }

    @Override
    @Transactional
    public void requestEarlyClosure(UUID shiftId) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with id: " + shiftId));

        if (shift.getStatus() != ShiftStatus.OPENED &&
                shift.getStatus() != ShiftStatus.OVERTIME) {
            throw new RuntimeException("Shift must be OPENED or OVERTIME to request early closure.");
        }

        shift.setStatus(ShiftStatus.CLOSING);
        shiftRepository.save(shift);
    }

    @Override
    public boolean hasActiveBookings(Shift shift) {
        // Check Airport Transfer Details
        if (shift.getAirportTransferDetails() != null) {
            for (var detail : shift.getAirportTransferDetails()) {
                if (detail.getServiceRequest() != null &&
                        detail.getServiceRequest().getStatus() == BookingStatus.RUNNING) {
                    return true;
                }
            }
        }
        // Check Tour Booking Details
        if (shift.getTourBookingDetails() != null) {
            for (var detail : shift.getTourBookingDetails()) {
                if (detail.getServiceRequest() != null &&
                        detail.getServiceRequest().getStatus() == BookingStatus.RUNNING) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    @Transactional
    public void finalizeShift(Shift shift, ShiftHistoryStatus finalStatus) {
        shift.setStatus(ShiftStatus.CLOSED);
        if (shift.getVehicle() != null) {
            shift.getVehicle().setStatus(VehicleStatus.AVAILABLE);
        }

        // Update current history record if exists
        shift.getShiftHistories().stream()
                .filter(ShiftHistory::isProcessing)
                .findFirst()
                .ifPresent(history -> {
                    history.setActualEndTime(LocalTime.now());
                    history.setFinalStatus(finalStatus);
                    history.setProcessing(false);
                    shiftHistoryRepository.save(history); // Explicitly save the update
                });

        shiftRepository.save(shift);
    }

    @Override
    @Transactional
    public ShiftResDto updateShift(UUID shiftId, ShiftUpdateReqDto dto) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with id: " + shiftId));

        if (dto.getDriverId() != null) {
            User driver = userRepository.findById(dto.getDriverId())
                    .filter(u -> u.getAudit().getIsActive())
                    .filter(u -> u.getRole().getName().equals("DRIVER"))
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found or inactive"));
            shift.addDriver(driver);
        }

        if (dto.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                    .filter(v -> v.getAudit().getIsActive())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found or inactive"));
            shift.addVehicle(vehicle);
        }

        if (dto.getStartDate() != null) shift.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) shift.setEndDate(dto.getEndDate());
        if (dto.getStartTime() != null) shift.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) shift.setEndTime(dto.getEndTime());
        if (dto.getNotes() != null) shift.setNotes(dto.getNotes());

        Shift savedShift = shiftRepository.save(shift);
        return shiftMapper.toShiftResDto(savedShift);
    }
}
