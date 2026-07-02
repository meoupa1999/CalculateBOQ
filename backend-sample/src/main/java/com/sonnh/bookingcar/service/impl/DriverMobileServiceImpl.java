package com.sonnh.bookingcar.service.impl;

import com.sonnh.bookingcar.data.domain.AirportTransferDetail;
import com.sonnh.bookingcar.data.domain.Shift;
import com.sonnh.bookingcar.data.domain.ShiftHistory;
import com.sonnh.bookingcar.data.domain.TourBookingDetail;
import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import com.sonnh.bookingcar.data.domain.enums.ImageType;
import com.sonnh.bookingcar.data.domain.enums.ShiftHistoryStatus;
import com.sonnh.bookingcar.data.domain.enums.ShiftStatus;
import com.sonnh.bookingcar.data.domain.enums.VehicleStatus;
import com.sonnh.bookingcar.data.domain.ServiceRequest;
import com.sonnh.bookingcar.data.domain.Vehicle;
import com.sonnh.bookingcar.data.domain.enums.ServiceType;

import com.sonnh.bookingcar.data.repository.UserRepository;
import com.sonnh.bookingcar.data.repository.AirportTransferDetailRepository;
import com.sonnh.bookingcar.data.repository.ServiceRequestRepository;
import com.sonnh.bookingcar.data.repository.RequestStatusHistoryRepository;
import com.sonnh.bookingcar.data.repository.ShiftHistoryRepository;
import com.sonnh.bookingcar.data.repository.ShiftRepository;
import com.sonnh.bookingcar.data.repository.TourBookingDetailRepository;
import com.sonnh.bookingcar.data.repository.ServiceSurchargeRepository;
import com.sonnh.bookingcar.dto.request.driver.StartShiftReqDto;
import com.sonnh.bookingcar.dto.request.driver.EndShiftReqDto;
import com.sonnh.bookingcar.dto.request.driver.NegotiatePriceReqDto;
import com.sonnh.bookingcar.dto.request.driver.AddSurchargeReqDto;
import com.sonnh.bookingcar.data.domain.ServiceSurcharge;
import com.sonnh.bookingcar.data.domain.VehicleIncident;
import com.sonnh.bookingcar.data.repository.VehicleIncidentRepository;
import com.sonnh.bookingcar.data.repository.VehicleRepository;
import com.sonnh.bookingcar.data.specification.DriverAirportTranferSpecification;
import com.sonnh.bookingcar.data.specification.DriverTourSpecification;
import com.sonnh.bookingcar.dto.response.admin.ShiftResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverAirportDetailResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverRequestResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverShiftDetailResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverSystemRequestResDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverTourDetailResDto;
import com.sonnh.bookingcar.dto.response.driver.TrackingSignalDTO;
import com.sonnh.bookingcar.dto.response.driver.WorkDaysResDto;
import com.sonnh.bookingcar.mapper.BookingMapper;
import com.sonnh.bookingcar.mapper.ShiftMapper;
import com.sonnh.bookingcar.service.interfaces.DriverMobileService;
import com.sonnh.bookingcar.dto.response.driver.DriverProfileResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverEarningsResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverVehicleDetailResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverDocumentResDto;
import com.sonnh.bookingcar.dto.request.driver.UpdateProfileReqDto;
import com.sonnh.bookingcar.data.domain.User;
import com.sonnh.bookingcar.data.domain.Image;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverMobileServiceImpl implements DriverMobileService {

    private final ShiftRepository shiftRepository;
    private final ShiftHistoryRepository shiftHistoryRepository;
    private final AirportTransferDetailRepository airportTransferDetailRepository;
    private final TourBookingDetailRepository tourBookingDetailRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final RequestStatusHistoryRepository requestStatusHistoryRepository;
    private final ServiceSurchargeRepository serviceSurchargeRepository;
    private final BookingMapper bookingMapper;
    private final ShiftMapper shiftMapper;
    // Store tracking requests with expiration time
    private final Map<UUID, LocalDateTime> trackingRequests = new ConcurrentHashMap<>();
    private LocalDateTime globalTrackingExpiry;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleIncidentRepository vehicleIncidentRepository;

    @Override
    @Transactional
    public void startShift(StartShiftReqDto dto) {
        Shift shift = shiftRepository.findById(dto.getShiftId())
                .orElseThrow(() -> new RuntimeException("Shift not found with id: " + dto.getShiftId()));

        if (shift.getStatus() != ShiftStatus.OPENED) {
            throw new RuntimeException("Shift is not in OPENED status. Current status: " + shift.getStatus());
        }

        if (shift.getVehicle() != null) {
            BigDecimal startMileage = dto.getStartMileage() != null ? dto.getStartMileage() : BigDecimal.ZERO;
            // 1. Mileage Chain Validation (Industry Standard)
            shiftHistoryRepository.findFirstByShift_VehicleIdOrderByAudit_CreatedAtDesc(shift.getVehicle().getId())
                    .ifPresent(prevShift -> {
                        BigDecimal prevEnd = prevShift.getEndMileage();
                        if (prevEnd != null && startMileage.compareTo(prevEnd) != 0) {
                            // Create Incident for discrepancy
                            VehicleIncident discrepancy = VehicleIncident.builder()
                                    .vehicle(shift.getVehicle())
                                    .type("DISCREPANCY")
                                    .severity("MEDIUM")
                                    .description("Sai lệch Km bàn giao. Trước đó trả xe: " + prevEnd + ", Nay nhận xe: "
                                            + startMileage)
                                    .build();
                            vehicleIncidentRepository.save(discrepancy);
                            log.warn("Mileage discrepancy detected for vehicle {}: PrevEnd={}, NewStart={}",
                                    shift.getVehicle().getPlateNumber(), prevEnd, startMileage);
                        }
                    });

            // 2. Maintenance Automation
            if (shift.getVehicle().getMaintenanceDueMileage() != null &&
                    startMileage.compareTo(shift.getVehicle().getMaintenanceDueMileage()) >= 0) {
                shift.getVehicle().setStatus(VehicleStatus.MAINTENANCE);
                log.info("Vehicle {} auto-flagged for MAINTENANCE due to mileage.",
                        shift.getVehicle().getPlateNumber());
            } else {
                if (shift.getVehicle().getStatus() == VehicleStatus.AVAILABLE) {
                    shift.getVehicle().setStatus(VehicleStatus.BUSY);
                }
            }
            // Sync current state to vehicle
            shift.getVehicle().setCurrentMileage(startMileage);
            if (dto.getStartFuelLevel() != null)
                shift.getVehicle().setCurrentFuelLevel(dto.getStartFuelLevel());
        }

        // Create ShiftHistory
        ShiftHistory history = ShiftHistory.builder()
                .actualStartDate(LocalDate.now())
                .actualStartTime(LocalTime.now())
                .startMileage(dto.getStartMileage())
                .startFuelLevel(dto.getStartFuelLevel())
                .isProcessing(true)
                .build();
        history.addShift(shift);
        shiftHistoryRepository.save(history);

        log.info("Driver {} started shift {} with vehicle {}. Start Mileage: {}, Start Fuel: {}%",
                shift.getDriver().getFullName(), shift.getId(),
                shift.getVehicle() != null ? shift.getVehicle().getPlateNumber() : "N/A",
                dto.getStartMileage(), dto.getStartFuelLevel());
    }

    @Override
    @Transactional
    public void endShift(EndShiftReqDto dto) {
        UUID shiftId = dto.getShiftId();
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new RuntimeException("Shift not found with id: " + shiftId));

        shift.setStatus(ShiftStatus.CLOSED);
        if (shift.getVehicle() != null) {
            BigDecimal endMileage = dto.getEndMileage() != null ? dto.getEndMileage() : BigDecimal.ZERO;

            // Update maintenance status if reached while driving
            if (shift.getVehicle().getMaintenanceDueMileage() != null &&
                    endMileage.compareTo(shift.getVehicle().getMaintenanceDueMileage()) >= 0) {
                shift.getVehicle().setStatus(VehicleStatus.MAINTENANCE);
            } else {
                shift.getVehicle().setStatus(VehicleStatus.AVAILABLE);
            }

            shift.getVehicle().setCurrentMileage(endMileage);
            if (dto.getEndFuelLevel() != null)
                shift.getVehicle().setCurrentFuelLevel(dto.getEndFuelLevel());
        }

        ShiftHistory history = shift.getShiftHistories().stream()
                .filter(h -> h.getId().equals(dto.getShiftHistoryId())
                        || (dto.getShiftHistoryId() == null && h.isProcessing()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Active shift history not found"));

        history.setActualEndTime(LocalTime.now());
        history.setFinalStatus(ShiftHistoryStatus.NORMAL);
        history.setEndMileage(dto.getEndMileage());
        history.setEndFuelLevel(dto.getEndFuelLevel());
        history.setHandoverNotes(dto.getNotes());
        history.setProcessing(false);

        // Process images if any
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            for (String url : dto.getImageUrls()) {
                Image img = Image.builder()
                        .filePath(url)
                        .imageType(ImageType.OTHER)
                        .build();
                img.addShiftHistory(history);
            }
        }

        recallUnfinishedBookings(shift);
        shiftRepository.save(shift);
        log.info("Shift {} ended. End Mileage: {}, End Fuel: {}%", shiftId, dto.getEndMileage(), dto.getEndFuelLevel());
    }

    @Override
    @Transactional
    public void confirmEarlyClosure(UUID shiftId, UUID historyId) {
        log.info("Received confirmEarlyClosure for shiftId: {}, historyId: {}", shiftId, historyId);
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new RuntimeException("Shift not found with id: " + shiftId));

        if (shift.getStatus() != ShiftStatus.CLOSING) {
            throw new RuntimeException("Shift is not in CLOSING status.");
        }

        shift.setStatus(ShiftStatus.CLOSED);
        if (shift.getVehicle() != null) {
            shift.getVehicle().setStatus(VehicleStatus.AVAILABLE);
        }

        // Update current history record
        List<ShiftHistory> histories = shift.getShiftHistories();
        log.info("Shift {} has {} histories", shift.getId(), histories != null ? histories.size() : 0);

        shift.getShiftHistories().stream()
                .filter(h -> h.getId().equals(historyId) || (historyId == null && h.isProcessing()))
                .findFirst()
                .ifPresentOrElse(history -> {
                    log.info("Found history to update: {}. Setting status to EARLY.", history.getId());
                    history.setActualEndTime(LocalTime.now());
                    history.setFinalStatus(ShiftHistoryStatus.EARLY);
                    history.setProcessing(false);
                }, () -> log.warn("No matching history found for historyId: {} or active history for shift: {}",
                        historyId, shiftId));

        // Recall unfinished bookings before closing early
        recallUnfinishedBookings(shift);

        log.info("Driver {} confirmed early closure for shift {}",
                shift.getDriver().getFullName(), shift.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public PageImplResDto<DriverRequestResDto> getAssignedRequests(UUID driverId, LocalDateTime startDate,
            LocalDateTime endDate, Integer page, Integer size) {
        List<DriverRequestResDto> result = new ArrayList<>();

        // Process Airport Transfers
        Set<BookingStatus> statuses = Set.of(
                BookingStatus.DISPATCHED,
                BookingStatus.ACCEPTED,
                BookingStatus.RUNNING);
        Specification<ServiceRequest> spec = Specification
                .where(DriverAirportTranferSpecification.distinct())
                .and(DriverAirportTranferSpecification.hasStatusIn(statuses))
                .and(DriverAirportTranferSpecification.inTimeRange(startDate, endDate))
                .and(DriverAirportTranferSpecification.hasDriverId(driverId));
        List<ServiceRequest> airportRequests = serviceRequestRepository.findAll(spec);

        // Process Tour Bookings
        Specification<ServiceRequest> specTour = Specification
                .where(DriverTourSpecification.distinct())
                .and(DriverTourSpecification.hasStatusIn(statuses))
                .and(DriverTourSpecification.inTimeRange(startDate, endDate))
                .and(DriverTourSpecification.hasDriverId(driverId));
        List<ServiceRequest> tourRequests = serviceRequestRepository.findAll(specTour);

        // Combine and Sort
        List<ServiceRequest> combinedRequests = new ArrayList<>();
        combinedRequests.addAll(airportRequests);
        combinedRequests.addAll(tourRequests);
        combinedRequests.sort(Comparator.comparing(this::getLatestDispatchedTime).reversed());

        // Paginate in memory
        int start = (page - 1) * size;
        int end = Math.min(start + size, combinedRequests.size());
        List<ServiceRequest> pagedRequests = (start < combinedRequests.size()) ? combinedRequests.subList(start, end)
                : new ArrayList<>();

        // Map to DTO
        List<DriverRequestResDto> content = pagedRequests.stream()
                .map(sr -> {
                    if (sr.getType() == ServiceType.AIRPORT) {
                        return bookingMapper.airportDetailToDriverRequestResDto(sr.getAirportTransferDetail());
                    } else {
                        return bookingMapper.tourDetailToDriverRequestResDto(sr.getTourBookingDetail());
                    }
                })
                .collect(Collectors.toList());

        Page<DriverRequestResDto> pageResult = new PageImpl<>(content, PageRequest.of(page - 1, size),
                combinedRequests.size());
        return PageImplResDto.fromPage(pageResult);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverRequestResDto> getBookingHistory(UUID driverId) {
        List<Shift> airportShifts = shiftRepository.findWithAirportDetailsByDriverId(driverId);
        List<Shift> tourShifts = shiftRepository.findWithTourDetailsByDriverId(driverId);
        List<DriverRequestResDto> result = new ArrayList<>();

        // Process Airport Transfers
        airportShifts.stream()
                .filter(s -> s.getAirportTransferDetails() != null)
                .flatMap(s -> s.getAirportTransferDetails().stream())
                .flatMap(d -> Stream.ofNullable(d.getServiceRequest()))
                .filter(sr -> sr.getStatus() == BookingStatus.REJECTED_BY_DRIVER ||
                        sr.getStatus() == BookingStatus.DONE ||
                        sr.getStatus() == BookingStatus.CANCELED_BY_DRIVER ||
                        sr.getStatus() == BookingStatus.CANCELLED ||
                        sr.getStatus() == BookingStatus.CANCELED_BY_ADMIN)
                .sorted(Comparator.comparing(this::getLatestStatusUpdateTime).reversed())
                .forEach(sr -> result
                        .add(bookingMapper.airportDetailToDriverRequestResDto(sr.getAirportTransferDetail())));

        // Process Tour Bookings
        tourShifts.stream()
                .filter(s -> s.getTourBookingDetails() != null)
                .flatMap(s -> s.getTourBookingDetails().stream())
                .flatMap(d -> Stream.ofNullable(d.getServiceRequest()))
                .filter(sr -> sr.getStatus() == BookingStatus.REJECTED_BY_DRIVER ||
                        sr.getStatus() == BookingStatus.DONE ||
                        sr.getStatus() == BookingStatus.CANCELED_BY_DRIVER ||
                        sr.getStatus() == BookingStatus.CANCELLED ||
                        sr.getStatus() == BookingStatus.CANCELED_BY_ADMIN)
                .sorted(Comparator.comparing(this::getLatestStatusUpdateTime).reversed())
                .forEach(sr -> result.add(bookingMapper.tourDetailToDriverRequestResDto(sr.getTourBookingDetail())));

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public DriverAirportDetailResDto getAirportRequestDetail(UUID requestId) {
        AirportTransferDetail detail = airportTransferDetailRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Airport Transfer not found with id: " + requestId));

        DriverAirportDetailResDto dto = bookingMapper.airportDetailToDriverAirportDetailResDto(detail);

        List<BookingStatus> cancelStatuses = List.of(
                BookingStatus.CANCELLED,
                BookingStatus.CANCELED_BY_ADMIN,
                BookingStatus.CANCELED_BY_DRIVER,
                BookingStatus.REJECTED_BY_DRIVER);

        if (detail.getServiceRequest() != null && cancelStatuses.contains(detail.getServiceRequest().getStatus())) {
            requestStatusHistoryRepository
                    .findCancelStatus(detail.getServiceRequest().getStatus(), detail.getServiceRequest().getId())
                    .ifPresent(h -> {
                        dto.setCancelReason(DriverAirportDetailResDto.CancelReasonResDto.builder()
                                .reasonNote(h.getReasonNote())
                                .role(h.getRole())
                                .userName(h.getActionBy() != null ? h.getActionBy().getFullName() : "Hệ thống")
                                .build());
                    });
        }

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public DriverTourDetailResDto getTourRequestDetail(UUID requestId) {
        TourBookingDetail detail = tourBookingDetailRepository.findByServiceRequestId(requestId)
                .orElseThrow(() -> new RuntimeException("Tour booking detail not found with request id: " + requestId));

        DriverTourDetailResDto dto = bookingMapper.tourDetailToDriverTourDetailResDto(detail);

        List<BookingStatus> cancelStatuses = List.of(
                BookingStatus.CANCELLED,
                BookingStatus.CANCELED_BY_ADMIN,
                BookingStatus.CANCELED_BY_DRIVER,
                BookingStatus.REJECTED_BY_DRIVER);

        if (detail.getServiceRequest() != null && cancelStatuses.contains(detail.getServiceRequest().getStatus())) {
            requestStatusHistoryRepository
                    .findCancelStatus(detail.getServiceRequest().getStatus(), detail.getServiceRequest().getId())
                    .ifPresent(h -> {
                        dto.setCancelReason(DriverTourDetailResDto.CancelReasonResDto.builder()
                                .reasonNote(h.getReasonNote())
                                .role(h.getRole())
                                .userName(h.getActionBy() != null ? h.getActionBy().getFullName() : "Hệ thống")
                                .build());
                    });
        }

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public ShiftResDto getMyShifts(UUID driverId) {
        List<Shift> shifts = shiftRepository.findByDriverId(driverId);
        LocalDateTime now = LocalDateTime.now();

        Shift bestCandidate = null;

        for (Shift s : shifts) {
            // Chỉ xét những ca chưa đóng
            if (s.getStatus() == ShiftStatus.CLOSED)
                continue;

            LocalDateTime shiftStart = LocalDateTime.of(s.getStartDate(), s.getStartTime());
            LocalDateTime shiftEnd = LocalDateTime.of(s.getEndDate(), s.getEndTime());

            // Hàng rào bảo vệ thời gian
            boolean isTimeValid = !now.isBefore(shiftStart) && !now.isAfter(shiftEnd);
            if (!isTimeValid)
                continue;

            // Kiểm tra xem đã có history nào đang xử lý chưa
            boolean hasActiveHistory = s.getShiftHistories() != null && s.getShiftHistories().stream()
                    .anyMatch(ShiftHistory::isProcessing);

            // ƯU TIÊN 1: Nếu tìm thấy ca đã BẮT ĐẦU (có history processing), trả về ngay
            // lập tức
            if (hasActiveHistory) {
                ShiftResDto dto = shiftMapper.toShiftResDto(s);
                dto.setStarted(true);
                return dto;
            }

            // ƯU TIÊN 2: Nếu chưa có ca nào started, lưu lại ca "Sẵn sàng" cuối cùng tìm
            // thấy
            if (bestCandidate == null) {
                bestCandidate = s;
            }
        }

        // Nếu có ca "Sẵn sàng" nhưng chưa started, trả về ca đó với started = false
        if (bestCandidate != null) {
            ShiftResDto dto = shiftMapper.toShiftResDto(bestCandidate);
            dto.setStarted(false);

            // Check if vehicle is busy by another driver (effectively final fix)
            final UUID currentShiftId = bestCandidate.getId();
            Vehicle v = bestCandidate.getVehicle();
            if (v != null && v.getStatus() == VehicleStatus.BUSY) {
                // Find the shift that's currently using the vehicle
                v.getShiftList().stream()
                        .filter(other -> other.getStatus() != ShiftStatus.CLOSED
                                && !other.getId().equals(currentShiftId))
                        .findFirst()
                        .ifPresent(activeShift -> {
                            dto.setVehicleBusy(true);
                            dto.setConflictDriverName(activeShift.getDriver().getFullName());
                            dto.setConflictDriverPhone(activeShift.getDriver().getPhone());
                        });
            }
            return dto;
        }

        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverSystemRequestResDto> getSystemRequests(UUID driverId) {
        List<Shift> closingShifts = shiftRepository.findByDriverId(driverId).stream()
                .filter(s -> s.getStatus() == ShiftStatus.CLOSING)
                .collect(Collectors.toList());

        return closingShifts.stream().map(s -> {
            UUID processingHistoryId = s.getShiftHistories().stream()
                    .filter(ShiftHistory::isProcessing)
                    .map(ShiftHistory::getId)
                    .findFirst().orElse(null);

            return DriverSystemRequestResDto.builder()
                    .id(s.getId())
                    .title("Yêu cầu kết thúc ca sớm")
                    .scheduledStartTime(s.getStartTime())
                    .scheduledEndTime(s.getEndTime())
                    .shiftHistoryId(processingHistoryId)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DriverShiftDetailResDto getShiftDetail(UUID shiftId) {
        Shift s = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        ShiftHistory processingHistory = s.getShiftHistories().stream()
                .filter(ShiftHistory::isProcessing)
                .findFirst().orElse(null);

        DriverShiftDetailResDto.ShiftHistoryResDto historyDto = null;
        if (processingHistory != null) {
            historyDto = DriverShiftDetailResDto.ShiftHistoryResDto.builder()
                    .id(processingHistory.getId())
                    .actualStartDate(processingHistory.getActualStartDate())
                    .actualStartTime(processingHistory.getActualStartTime())
                    .actualEndTime(processingHistory.getActualEndTime())
                    .finalStatus(processingHistory.getFinalStatus())
                    .isProcessing(processingHistory.isProcessing())
                    .build();
        }

        return DriverShiftDetailResDto.builder()
                .id(s.getId())
                .startDate(s.getStartDate())
                .endDate(s.getEndDate())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .status(s.getStatus())
                .currentHistory(historyDto)
                .build();
    }

    private void recallUnfinishedBookings(Shift shift) {
        // Xử lý Airport Transfer
        if (shift.getAirportTransferDetails() != null) {
            List<AirportTransferDetail> airportTransferDetailList = new ArrayList<>(shift.getAirportTransferDetails());
            airportTransferDetailList.stream()
                    .map(detail -> {
                        ServiceRequest req = detail.getServiceRequest();
                        if (req != null && (req.getStatus() == BookingStatus.ACCEPTED
                                || req.getStatus() == BookingStatus.DISPATCHED)) {
                            detail.deleteShift(shift);
                        }
                        return req;
                    })
                    .forEach(req -> {
                        if (req != null && (req.getStatus() == BookingStatus.ACCEPTED
                                || req.getStatus() == BookingStatus.DISPATCHED)) {
                            log.info("Recalling Airport Transfer {} to WAITING", req.getId());
                            req.setStatus(BookingStatus.WAITING);
                            serviceRequestRepository.save(req);
                        }
                    });
        }

        // Xử lý Tour Booking
        if (shift.getTourBookingDetails() != null) {
            List<TourBookingDetail> tourBookingDetailList = new ArrayList<>(shift.getTourBookingDetails());
            tourBookingDetailList.stream()
                    .map(detail -> {
                        ServiceRequest req = detail.getServiceRequest();
                        if (req != null && (req.getStatus() == BookingStatus.ACCEPTED
                                || req.getStatus() == BookingStatus.DISPATCHED)) {
                            detail.deleteShift(shift);
                        }
                        return req;
                    })
                    .forEach(req -> {
                        if (req != null && (req.getStatus() == BookingStatus.ACCEPTED
                                || req.getStatus() == BookingStatus.DISPATCHED)) {
                            log.info("Recalling Tour Booking {} to WAITING", req.getId());
                            req.setStatus(BookingStatus.WAITING);
                            serviceRequestRepository.save(req);
                        }
                    });
        }
    }

    // @Override
    // @Transactional
    // public void updateLocation(DriverLocationReqDto dto) {
    // UUID driverId = dto.getDriverId();
    // DriverLocation location = driverLocationRepository.findByDriverId(driverId)
    // .orElseGet(() -> {
    // User driver = userRepository.findById(driverId)
    // .orElseThrow(() -> new RuntimeException("Driver not found with id: " +
    // driverId));
    // return DriverLocation.builder()
    // .driver(driver)
    // .build();
    // });

    // location.setLatitude(dto.getLatitude());
    // location.setLongitude(dto.getLongitude());
    // location.getAudit().setUpdatedAt(LocalDateTime.now());

    // driverLocationRepository.save(location);
    // }

    // @Override
    // @Transactional(readOnly = true)
    // public List<DriverLocationResDto> getAllDriverLocation(double lat, double
    // lng) {
    // return driverLocationRepository.findAll().stream()
    // .filter(this::isFresh)
    // .map(d -> DriverLocationResDto.builder()
    // .driverId(d.getDriver().getId())
    // .latitude(d.getLatitude())
    // .longitude(d.getLongitude())
    // .build())
    // .collect(Collectors.toList());
    // }

    // @Override
    // @Transactional(readOnly = true)
    // public DriverLocationResDto getDriverLocation(UUID driverId) {
    // return driverLocationRepository.findByDriverId(driverId)
    // .filter(this::isFresh)
    // .map(d -> DriverLocationResDto.builder()
    // .driverId(d.getDriver().getId())
    // .latitude(d.getLatitude())
    // .longitude(d.getLongitude())
    // .build())
    // .orElse(null);
    // }

    @Override
    public void startTracking(UUID driverId) {
        log.info("Starting tracking for driver: {}", driverId);
        trackingRequests.put(driverId, LocalDateTime.now().plusMinutes(2));
        messagingTemplate.convertAndSend("/topic/tracking/driver/" + driverId, new TrackingSignalDTO(true));
    }

    @Override
    public void stopTracking(UUID driverId) {
        log.info("Stopping tracking for driver: {}", driverId);
        trackingRequests.remove(driverId);
        messagingTemplate.convertAndSend("/topic/tracking/driver/" + driverId, new TrackingSignalDTO(false));
    }

    @Override
    public void startGlobalTracking() {
        log.info("Starting GLOBAL tracking");
        globalTrackingExpiry = LocalDateTime.now().plusMinutes(2);
        messagingTemplate.convertAndSend("/topic/tracking/global", new TrackingSignalDTO(true));
    }

    @Override
    public void stopGlobalTracking() {
        log.info("Stopping GLOBAL tracking");
        globalTrackingExpiry = null;
        messagingTemplate.convertAndSend("/topic/tracking/global", new TrackingSignalDTO(false));
    }

    // @Override
    // public DriverTrackingStatusResDto getTrackingStatus(UUID driverId) {
    // return DriverTrackingStatusResDto.builder()
    // .trackingRequired(isTrackingRequested(driverId))
    // .build();
    // }

    // private boolean isTrackingRequested(UUID driverId) {
    // boolean globalActive = false;
    // boolean specificActive = false;

    // // Check Global Tracking
    // if (globalTrackingExpiry != null) {
    // if (LocalDateTime.now().isBefore(globalTrackingExpiry)) {
    // globalActive = true;
    // } else {
    // log.info("[Tracking] Global tracking EXPIRED at {}", globalTrackingExpiry);
    // globalTrackingExpiry = null;
    // }
    // }

    // // Check Specific Driver Tracking
    // LocalDateTime expiry = trackingRequests.get(driverId);
    // if (expiry != null) {
    // if (LocalDateTime.now().isBefore(expiry)) {
    // specificActive = true;
    // } else {
    // log.info("[Tracking] Specific tracking for driver {} EXPIRED at {}",
    // driverId, expiry);
    // trackingRequests.remove(driverId);
    // }
    // }

    // if (globalActive || specificActive) {
    // log.debug("[Tracking] Tracking status for {}: Global={}, Specific={}",
    // driverId, globalActive, specificActive);
    // return true;
    // }

    // return false;
    // }

    // private boolean isFresh(DriverLocation d) {
    // if (d.getAudit().getUpdatedAt() == null) {
    // return false;
    // }
    // return Duration.between(d.getAudit().getUpdatedAt(),
    // LocalDateTime.now()).getSeconds() < 120;
    // }
    @Override
    @Transactional(readOnly = true)
    public DriverProfileResDto getDriverProfile(UUID driverId) {
        User driver = shiftRepository.findByDriverId(driverId).stream()
                .map(Shift::getDriver)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

        // Get active or most recent vehicle info (Sorted by recency)
        Optional<Shift> activeShift = shiftRepository.findByDriverId(driverId).stream()
                .filter(s -> s.getStatus() != ShiftStatus.CLOSED)
                .sorted((s1, s2) -> {
                    LocalDateTime t1 = s1.getAudit() != null && s1.getAudit().getUpdatedAt() != null
                            ? s1.getAudit().getUpdatedAt()
                            : LocalDateTime.MIN;
                    LocalDateTime t2 = s2.getAudit() != null && s2.getAudit().getUpdatedAt() != null
                            ? s2.getAudit().getUpdatedAt()
                            : LocalDateTime.MIN;
                    return t2.compareTo(t1);
                })
                .findFirst();

        Vehicle vehicle = activeShift.isPresent() ? activeShift.get().getVehicle() : null;

        return DriverProfileResDto.builder()
                .id(driver.getId())
                .fullName(driver.getFullName())
                .phone(driver.getPhone())
                .email(driver.getEmail())
                .profileImage(driver.getProfileImage())
                .rating(driver.getDriverRating())
                .vehicleModel(vehicle != null ? vehicle.getModel() : "Chưa nhận xe")
                .plateNumber(vehicle != null ? vehicle.getPlateNumber() : "N/A")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DriverEarningsResDto getDriverEarnings(UUID driverId) {
        List<Shift> airportShifts = shiftRepository.findWithAirportDetailsByDriverId(driverId);
        List<Shift> tourShifts = shiftRepository.findWithTourDetailsByDriverId(driverId);

        List<ServiceRequest> completedRequests = new ArrayList<>();

        airportShifts.stream()
                .filter(s -> s.getAirportTransferDetails() != null)
                .flatMap(s -> s.getAirportTransferDetails().stream())
                .map(AirportTransferDetail::getServiceRequest)
                .filter(sr -> sr != null && sr.getStatus() == BookingStatus.DONE)
                .forEach(completedRequests::add);

        tourShifts.stream()
                .filter(s -> s.getTourBookingDetails() != null)
                .flatMap(s -> s.getTourBookingDetails().stream())
                .map(TourBookingDetail::getServiceRequest)
                .filter(sr -> sr != null && sr.getStatus() == BookingStatus.DONE)
                .forEach(completedRequests::add);

        BigDecimal totalEarnings = completedRequests.stream()
                .map(sr -> {
                    if (sr.getDriverAmount() != null)
                        return sr.getDriverAmount();
                    if (sr.getTotalPrice() != null)
                        return sr.getTotalPrice(); // Fallback to totalPrice
                    return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<LocalDate, BigDecimal> dailyEarnings = completedRequests.stream()
                .filter(sr -> sr.getAudit() != null && sr.getAudit().getUpdatedAt() != null)
                .collect(Collectors.groupingBy(
                        sr -> sr.getAudit().getUpdatedAt().toLocalDate(),
                        Collectors.reducing(BigDecimal.ZERO,
                                sr -> {
                                    if (sr.getDriverAmount() != null)
                                        return sr.getDriverAmount();
                                    if (sr.getTotalPrice() != null)
                                        return sr.getTotalPrice();
                                    return BigDecimal.ZERO;
                                },
                                BigDecimal::add)));

        List<DriverEarningsResDto.RecentTransaction> transactions = completedRequests.stream()
                .filter(sr -> sr.getAudit() != null && sr.getAudit().getUpdatedAt() != null)
                .sorted(Comparator.comparing((ServiceRequest sr) -> sr.getAudit().getUpdatedAt()).reversed())
                .limit(10)
                .map(sr -> {
                    String title = sr.getType().toString();
                    String sub = sr.getBookingCode();
                    if (sr.getAirportTransferDetail() != null) {
                        title = sr.getAirportTransferDetail().getShortPickupLocation() + " → "
                                + sr.getAirportTransferDetail().getShortDropoffLocation();
                    } else if (sr.getTourBookingDetail() != null) {
                        title = "Tour: " + sr.getTourBookingDetail().getPickupLocation();
                    }

                    BigDecimal amount = sr.getDriverAmount() != null ? sr.getDriverAmount() : sr.getTotalPrice();

                    return DriverEarningsResDto.RecentTransaction.builder()
                            .title(title)
                            .sub(sub)
                            .amount(amount)
                            .type("plus")
                            .status("DONE")
                            .date(sr.getAudit().getUpdatedAt().toString())
                            .build();
                })
                .collect(Collectors.toList());

        return DriverEarningsResDto.builder()
                .totalEarnings(totalEarnings)
                .dailyEarnings(dailyEarnings)
                .recentTransactions(transactions)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DriverVehicleDetailResDto getVehicleDetail(UUID driverId) {
        // Find most recent active shift
        Optional<Shift> activeShift = shiftRepository.findByDriverId(driverId).stream()
                .filter(s -> s.getStatus() != ShiftStatus.CLOSED)
                .sorted((s1, s2) -> {
                    LocalDateTime t1 = s1.getAudit() != null && s1.getAudit().getUpdatedAt() != null
                            ? s1.getAudit().getUpdatedAt()
                            : LocalDateTime.MIN;
                    LocalDateTime t2 = s2.getAudit() != null && s2.getAudit().getUpdatedAt() != null
                            ? s2.getAudit().getUpdatedAt()
                            : LocalDateTime.MIN;
                    return t2.compareTo(t1);
                })
                .findFirst();

        Vehicle vehicle = activeShift.isPresent() ? activeShift.get().getVehicle() : null;

        if (vehicle == null) {
            return null;
        }

        return DriverVehicleDetailResDto.builder()
                .id(vehicle.getId())
                .model(vehicle.getModel())
                .plateNumber(vehicle.getPlateNumber())
                .vehicleType(vehicle.getVehicleType() != null ? vehicle.getVehicleType().getId() : null)
                .type(vehicle.getVehicleType() != null ? vehicle.getVehicleType().getName() : vehicle.getType())
                .status(vehicle.getStatus())
                .year(vehicle.getYear())
                .color(vehicle.getColor())
                .ownershipType(vehicle.getOwnershipType())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverDocumentResDto> getDriverDocuments(UUID driverId) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        List<DriverDocumentResDto> result = new ArrayList<>();

        // Add regular documents
        if (driver.getDocuments() != null) {
            driver.getDocuments().forEach(doc -> {
                result.add(DriverDocumentResDto.builder()
                        .id(doc.getId())
                        .documentType(doc.getDocumentType() != null ? doc.getDocumentType().toString() : "UNKNOWN")
                        .documentNumber(doc.getDocumentNumber())
                        .issuedDate(doc.getIssuedDate())
                        .expiredDate(doc.getExpiredDate())
                        .status("VERIFIED") // Standard for existing docs in this context
                        .imageUrls(doc.getImages() != null
                                ? doc.getImages().stream().map(Image::getFilePath).collect(Collectors.toList())
                                : new ArrayList<>())
                        .build());
            });
        }

        // Add license verification status as a document entry
        // (Assuming you have a LicenseVerificationRepository or access via User)
        // For simplicity, we can fetch from the User's linked licenses if mapped.
        // If not mapped, we'd need another repository. Let's stick to Documents for now
        // as it's more comprehensive.

        return result;
    }

    @Override
    @Transactional
    public void updateProfile(UUID driverId, UpdateProfileReqDto dto) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        if (dto.getFullName() != null)
            driver.setFullName(dto.getFullName());
        if (dto.getEmail() != null)
            driver.setEmail(dto.getEmail());
        if (dto.getProfileImage() != null)
            driver.setProfileImage(dto.getProfileImage());

        userRepository.save(driver);
    }

    @Override
    @Transactional
    public void negotiatePrice(UUID driverId, NegotiatePriceReqDto dto) {
        ServiceRequest request = serviceRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setNegotiatedPrice(dto.getNegotiatedPrice());
        request.setTotalPrice(dto.getNegotiatedPrice());
        request.setNegotiated(true);

        // Optionally update driverAmount logic here
        serviceRequestRepository.save(request);
        log.info("Price negotiated for request {}: {}", dto.getRequestId(), dto.getNegotiatedPrice());
    }

    @Override
    @Transactional
    public void addSurcharge(UUID driverId, AddSurchargeReqDto dto) {
        ServiceRequest request = serviceRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new RuntimeException("Request not found"));

        ServiceSurcharge surcharge = ServiceSurcharge.builder()
                .description(dto.getDescription())
                .amount(dto.getAmount())
                .build();
        surcharge.addServiceRequest(request);

        serviceSurchargeRepository.save(surcharge);

        // Update total price to reflect surcharges
        BigDecimal newTotal = request.getTotalPrice().add(dto.getAmount());
        request.setTotalPrice(newTotal);
        serviceRequestRepository.save(request);

        log.info("Added surcharge to request {}: {} - {}", dto.getRequestId(), dto.getDescription(), dto.getAmount());
    }

    @Override
    public List<WorkDaysResDto> getWorkDays(UUID driverId, Integer year, Integer month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = YearMonth.of(year, month).atEndOfMonth();
        List<WorkDaysResDto> workDays = new ArrayList<>();
        Set<BookingStatus> statuses = Set.of(
                BookingStatus.DISPATCHED,
                BookingStatus.ACCEPTED,
                BookingStatus.RUNNING);
        List<ServiceRequest> requests = serviceRequestRepository.getInRangeDate(startDate, endDate, statuses, driverId);
        requests.stream().forEach(
                sr -> {
                    if (sr.getType().equals(ServiceType.AIRPORT)) {
                        workDays.add(WorkDaysResDto.builder()
                                .date(sr.getAirportTransferDetail().getPickupDate())
                                .build());
                    } else {
                        workDays.add(WorkDaysResDto.builder()
                                .date(sr.getTourBookingDetail().getPickupDate())
                                .build());
                    }
                });
        return workDays;
    }

    private LocalDateTime getLatestDispatchedTime(ServiceRequest sr) {
        return sr.getStatusHistories().stream()
                .filter(h -> h.getStatus() == BookingStatus.DISPATCHED)
                .map(h -> h.getAudit().getUpdatedAt())
                .max(Comparator.naturalOrder())
                .orElse(sr.getAudit().getCreatedAt());
    }

    private LocalDateTime getLatestStatusUpdateTime(ServiceRequest sr) {
        return sr.getStatusHistories().stream()
                .map(h -> h.getAudit().getUpdatedAt())
                .max(Comparator.naturalOrder())
                .orElse(sr.getAudit().getCreatedAt());
    }
}
