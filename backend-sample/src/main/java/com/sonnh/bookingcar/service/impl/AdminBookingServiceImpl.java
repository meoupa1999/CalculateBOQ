package com.sonnh.bookingcar.service.impl;

import com.sonnh.bookingcar.data.domain.*;
import com.sonnh.bookingcar.data.repository.*;
import com.sonnh.bookingcar.data.specification.ServiceRequestSpecification;
import com.sonnh.bookingcar.dto.request.admin.BookingDispatchReqDto;
import com.sonnh.bookingcar.dto.response.admin.AdminBookingResDto;
import com.sonnh.bookingcar.dto.response.admin.AdminAirportBookingResDto;
import com.sonnh.bookingcar.dto.response.admin.AdminDashboardSummaryResDto;
import com.sonnh.bookingcar.dto.response.admin.AdminTourBookingResDto;
import com.sonnh.bookingcar.dto.response.admin.MilestoneResDto;
import com.sonnh.bookingcar.dto.response.admin.ShiftResDto;
import com.sonnh.bookingcar.dto.response.driver.BookingDispatchedAssigned;
import com.sonnh.bookingcar.dto.response.driver.DriverRequestResDto;
import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import com.sonnh.bookingcar.data.domain.enums.ServiceType;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.tourist.BookingCreatedEvent;
import com.sonnh.bookingcar.dto.response.tourist.TouristEstimateResDto;
import com.sonnh.bookingcar.dto.request.tourist.TripEstimateReqDto;
import com.sonnh.bookingcar.data.specification.ShiftSpecification;
import com.sonnh.bookingcar.mapper.BookingMapper;
import com.sonnh.bookingcar.mapper.ShiftMapper;
import com.sonnh.bookingcar.mapper.VehicleTypeMapper;
import com.sonnh.bookingcar.service.interfaces.AdminBookingService;
import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AdminBookingServiceImpl implements AdminBookingService {

        private final ServiceRequestRepository serviceRequestRepository;
        private final ShiftRepository shiftRepository;
        private final RequestStatusHistoryRepository requestStatusHistoryRepository;
        private final BookingMapper bookingMapper;
        private final ShiftMapper shiftMapper;
        private final VehicleTypeRepository vehicleTypeRepository;
        private final VehicleTypeMapper vehicleTypeMapper;
        private final ApplicationEventPublisher eventPublisher;

        @Override
        @Transactional(readOnly = true)
        public PageImplResDto<AdminBookingResDto> getBookings(Integer page, Integer size, String search,
                        BookingStatus status, ServiceType type) {
                Pageable pageable = PageRequest.of(
                                page != null && page > 0 ? page - 1 : 0,
                                size != null && size > 0 ? size : 10,
                                Sort.by(Sort.Direction.DESC, "audit.updatedAt"));

                Specification<ServiceRequest> spec = ServiceRequestSpecification.isActive();

                // Add status filter if provided
                if (status != null) {
                        spec = spec.and(ServiceRequestSpecification.hasStatus(status));
                }

                // Add type filter if provided
                if (type != null) {
                        spec = spec.and(ServiceRequestSpecification.hasType(type));
                }

                // If search is empty, we show all active requests (both AIRPORT and TOUR)
                if (search != null && !search.isEmpty()) {
                        spec = spec.and(ServiceRequestSpecification.search(search));
                }

                Page<ServiceRequest> requests = serviceRequestRepository.findAll(spec, pageable);
                Page<AdminBookingResDto> dtoPage = requests.map(rq -> {
                        AdminBookingResDto dto = bookingMapper.toAdminBookingResDto(rq);
                        dto.setEstimatedEndDate(calculateEstimatedEndDate(rq.getAirportTransferDetail()));
                        return dto;
                });

                return PageImplResDto.fromPage(dtoPage);
        }

        @Override
        @Transactional(readOnly = true)
        public AdminAirportBookingResDto getAirportBookingById(UUID id) {
                ServiceRequest req = serviceRequestRepository.findById(id)
                                .filter(r -> r.getAudit().getIsActive())
                                .orElseThrow(() -> new RuntimeException("Booking not found or inactive"));
                AdminAirportBookingResDto dto = bookingMapper.toAdminAirportBookingResDto(req);
                dto.setEstimatedEndDate(calculateEstimatedEndDate(req.getAirportTransferDetail()));
                java.util.List<BookingStatus> cancelStatuses = java.util.List.of(
                                BookingStatus.CANCELLED,
                                BookingStatus.CANCELED_BY_ADMIN,
                                BookingStatus.CANCELED_BY_DRIVER,
                                BookingStatus.REJECTED_BY_DRIVER);

                if (cancelStatuses.contains(req.getStatus())) {
                        requestStatusHistoryRepository.findCancelStatus(req.getStatus(), req.getId())
                                        .ifPresent(h -> {
                                                dto.setCancelReason(AdminAirportBookingResDto.CancelReasonResDto
                                                                .builder()
                                                                .reasonNote(h.getReasonNote())
                                                                .role(h.getRole())
                                                                .userName(h.getActionBy() != null
                                                                                ? h.getActionBy().getFullName()
                                                                                : "Hệ thống")
                                                                .build());
                                        });
                }

                return dto;
        }

        @Override
        @Transactional(readOnly = true)
        public AdminTourBookingResDto getTourBookingById(UUID id) {
                ServiceRequest req = serviceRequestRepository.findById(id)
                                .filter(r -> r.getAudit().getIsActive())
                                .orElseThrow(() -> new RuntimeException("Booking not found or inactive"));
                AdminTourBookingResDto dto = bookingMapper.toAdminTourBookingResDto(req);
                dto.setTime(req.getTourBookingDetail().getActualPickupTime().toString());

                java.util.List<BookingStatus> cancelStatuses = java.util.List.of(
                                BookingStatus.CANCELLED,
                                BookingStatus.CANCELED_BY_ADMIN,
                                BookingStatus.CANCELED_BY_DRIVER,
                                BookingStatus.REJECTED_BY_DRIVER);

                if (cancelStatuses.contains(req.getStatus())) {
                        requestStatusHistoryRepository.findCancelStatus(req.getStatus(), req.getId())
                                        .ifPresent(h -> {
                                                dto.setCancelReason(AdminTourBookingResDto.CancelReasonResDto.builder()
                                                                .reasonNote(h.getReasonNote())
                                                                .role(h.getRole())
                                                                .userName(h.getActionBy() != null
                                                                                ? h.getActionBy().getFullName()
                                                                                : "Hệ thống")
                                                                .build());
                                        });
                }

                return dto;
        }

        @Override
        @Transactional(readOnly = true)
        public AdminDashboardSummaryResDto getDashboardSummary() {
                return AdminDashboardSummaryResDto.builder()
                                .total(serviceRequestRepository.countAllActiveStatus())
                                .done(serviceRequestRepository.countByStatus(BookingStatus.DONE))
                                .waiting(serviceRequestRepository.countByStatus(BookingStatus.WAITING))
                                .processing(serviceRequestRepository.countByProcessingStatus())
                                .build();
        }

        @Override
        @Transactional(readOnly = true)
        public List<ShiftResDto> getAvailableShiftsForBooking(UUID bookingId) {

                ServiceRequest req = serviceRequestRepository.findById(bookingId)
                                .filter(r -> r.getAudit().getIsActive())
                                .orElseThrow(() -> new RuntimeException("Booking not found or inactive"));
                List<Shift> shifts = null;
                LocalDate pickupDate = null;
                LocalTime pickupTime = null;
                Shift currentShift = null;

                if (req.getAirportTransferDetail() != null) {
                        AirportTransferDetail detail = req.getAirportTransferDetail();
                        pickupDate = detail.getPickupDate();
                        pickupTime = detail.getPickupTime();
                        currentShift = detail.getCurrentShift();
                        shifts = shiftRepository.findShiftByVehicleTypeId(detail.getVehicleType().getId());
                } else if (req.getTourBookingDetail() != null) {
                        TourBookingDetail detail = req.getTourBookingDetail();
                        pickupDate = detail.getPickupDate();
                        pickupTime = LocalTime.of(8, 0); // Default for tours if not specified
                        currentShift = detail.getCurrentShift();
                        shifts = shiftRepository.findShiftByVehicleTypeId(
                                        detail.getVehicleTypePrice().getVehicleType().getId());
                }

                if (pickupDate == null || pickupTime == null) {
                        throw new RuntimeException("Invalid booking details for shift matching");
                }

                final LocalDate finalDate = pickupDate;
                final LocalTime finalTime = pickupTime;

                List<Shift> matchedShifts = new ArrayList<>(shifts.stream()
                                .filter(s -> !s.getStartDate().isAfter(finalDate)
                                                && !s.getEndDate().isBefore(finalDate))
                                .filter(s -> !s.getStartTime().isAfter(finalTime)
                                                && !s.getEndTime().isBefore(finalTime))
                                .collect(Collectors.toList()));

                // Include current shift if exists
                if (currentShift != null) {
                        final UUID currentId = currentShift.getId();
                        boolean alreadyIncluded = matchedShifts.stream()
                                        .anyMatch(s -> s.getId().equals(currentId));
                        if (!alreadyIncluded) {
                                matchedShifts.add(currentShift);
                        }
                }
                // xử lý soft warning trùng lịch

                final LocalDateTime reqStartDateTime;
                final LocalDateTime reqEndDateTime;
                if (req.getAirportTransferDetail() != null) {
                        reqStartDateTime = LocalDateTime.of(req.getAirportTransferDetail().getPickupDate(),
                                        req.getAirportTransferDetail().getPickupTime());
                        reqEndDateTime = req.getEstimateEndTime();
                } else if (req.getTourBookingDetail() != null) {
                        reqStartDateTime = LocalDateTime.of(req.getTourBookingDetail().getPickupDate(),
                                        req.getTourBookingDetail().getActualPickupTime());
                        reqEndDateTime = req.getEstimateEndTime();
                } else {
                        reqStartDateTime = null;
                        reqEndDateTime = null;
                }

                return matchedShifts.stream()
                                .sorted(Comparator.comparingDouble(this::calculateScore).reversed())
                                .map(shift -> {
                                        ShiftResDto shiftResDto = shiftMapper.toShiftResDto(shift);
                                        // airport case
                                        if (shift.getAirportTransferDetails() != null) {
                                                shift.getAirportTransferDetails()
                                                                .stream()
                                                                .forEach(atd -> {
                                                                        if (atd.getServiceRequest()
                                                                                        .getEstimateEndTime() != null) {
                                                                                if (reqStartDateTime.isBefore(
                                                                                                atd.getServiceRequest()
                                                                                                                .getEstimateEndTime())
                                                                                                && reqEndDateTime
                                                                                                                .isAfter(
                                                                                                                                LocalDateTime.of(
                                                                                                                                                atd
                                                                                                                                                                .getPickupDate(),
                                                                                                                                                atd.getPickupTime()))) {
                                                                                        shiftResDto.setConflicted(true);
                                                                                        shiftResDto.getDuplicateBookingIds()
                                                                                                        .add(atd.getServiceRequest()
                                                                                                                        .getId());
                                                                                }
                                                                        }

                                                                });
                                        } else if (shift.getTourBookingDetails() != null) {
                                                shift.getTourBookingDetails()
                                                                .stream()
                                                                .forEach(tbd -> {
                                                                        if (tbd.getServiceRequest()
                                                                                        .getEstimateEndTime() != null) {
                                                                                if (reqStartDateTime.isBefore(
                                                                                                tbd.getServiceRequest()
                                                                                                                .getEstimateEndTime())
                                                                                                && reqEndDateTime
                                                                                                                .isAfter(tbd
                                                                                                                                .getServiceRequest()
                                                                                                                                .getEstimateEndTime())) {
                                                                                        shiftResDto.setConflicted(true);
                                                                                        shiftResDto.getDuplicateBookingIds()
                                                                                                        .add(tbd.getServiceRequest()
                                                                                                                        .getId());
                                                                                }
                                                                        }

                                                                });
                                        }

                                        return shiftResDto;
                                })
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public void dispatchBooking(UUID id, BookingDispatchReqDto dto) {
                ServiceRequest req = serviceRequestRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Booking not found"));

                Shift shift = shiftRepository.findById(dto.getShiftId())
                                .orElseThrow(() -> new RuntimeException("Shift not found"));

                if (req.getAirportTransferDetail() != null) {
                        req.getAirportTransferDetail().addShift(shift);
                } else if (req.getTourBookingDetail() != null) {
                        req.getTourBookingDetail().addShift(shift);
                } else {
                        throw new RuntimeException("No detail found to dispatch");
                }

                req.setStatus(BookingStatus.DISPATCHED);
                serviceRequestRepository.save(req);

                // Record history
                RequestStatusHistory statusHistory = RequestStatusHistory.builder()
                                .status(BookingStatus.DISPATCHED)
                                .role("ADMIN")
                                .build();
                statusHistory.addServiceRequest(req);
                statusHistory.addDispatchedDriver(shift.getDriver());
                requestStatusHistoryRepository.save(statusHistory);

                System.out.println(" DEBUG: Dispatching booking " + req.getBookingCode() + " to driver "
                                + shift.getDriver().getFullName());

                // Publish event for real-time update
                DriverRequestResDto driverDto;
                if (req.getType() == ServiceType.AIRPORT) {
                        driverDto = bookingMapper.airportDetailToDriverRequestResDto(req.getAirportTransferDetail());
                } else {
                        driverDto = bookingMapper.tourDetailToDriverRequestResDto(req.getTourBookingDetail());
                }

                eventPublisher.publishEvent(
                                BookingDispatchedAssigned.builder()
                                                .assignedRequests(driverDto)
                                                .build());
        }

        @Override
        @Transactional
        public void cancelRequest(UUID id, String reasonNote) {
                ServiceRequest req = serviceRequestRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Booking not found"));

                if (req.getStatus() == BookingStatus.CANCELLED) {
                        throw new RuntimeException("Booking is already CANCELLED");
                }

                if (reasonNote == null || reasonNote.trim().isEmpty()) {
                        throw new RuntimeException("Reason note is required for cancellation");
                }

                req.setStatus(BookingStatus.CANCELED_BY_ADMIN);
                serviceRequestRepository.save(req);

                // Record history with CANCELED_BY_ADMIN status and reason
                RequestStatusHistory statusHistory = RequestStatusHistory.builder()
                                .status(BookingStatus.CANCELED_BY_ADMIN)
                                .role("ADMIN")
                                .reasonNote(reasonNote)
                                .build();
                statusHistory.addServiceRequest(req);
                requestStatusHistoryRepository.save(statusHistory);
        }

        public Map<String, Object> getRoute(double lat1, double lon1, double lat2, double lon2) {
                String url = String.format(Locale.US,
                                "http://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=full&geometries=polyline",
                                lon1, lat1, lon2, lat2);

                System.out.println("Fetching route from OSRM: " + url);
                RestTemplate restTemplate = new RestTemplate();
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);

                if (response == null || !response.containsKey("routes")) {
                        System.err.println("OSRM Response: " + response);
                        throw new RuntimeException("Could not fetch route from OSRM");
                }

                List<?> routes = (List<?>) response.get("routes");
                if (routes == null || routes.isEmpty()) {
                        throw new RuntimeException("No routes found");
                }

                return (Map<String, Object>) routes.get(0);
        }

        @Override
        public TouristEstimateResDto estimate(TripEstimateReqDto request) {
                Map<String, Object> route = this.getRoute(
                                request.getPickupLat(),
                                request.getPickupLon(),
                                request.getDestLat(),
                                request.getDestLon());

                double distance = ((Number) route.get("distance")).doubleValue(); // mét
                double duration = ((Number) route.get("duration")).doubleValue() / 60;
                String geometry = (String) route.get("geometry");

                double distanceKm = distance / 1000;

                List<TouristEstimateResDto.VehiclesPricingDto> vehiclePricing = vehicleTypeRepository.findAll().stream()
                                .filter(vt -> vt.getAudit().getIsActive())
                                .map(vt -> {
                                        TouristEstimateResDto.VehiclesPricingDto dto = vehicleTypeMapper
                                                        .toVehiclesPricingDto(vt);

                                        double basePrice = vt.getBasePrice() != null ? vt.getBasePrice().doubleValue()
                                                        : 200000;
                                        double pricePerKm = vt.getPricePerKm() != null
                                                        ? vt.getPricePerKm().doubleValue()
                                                        : 10000;
                                        double baseKm = vt.getBaseKm() != null ? vt.getBaseKm() : 0;

                                        double rawPrice = basePrice + Math.max(0, distanceKm - baseKm) * pricePerKm;
                                        double calculatedPrice = Math.round(rawPrice / 1000.0) * 1000.0;

                                        dto.setPrice(calculatedPrice);
                                        return dto;
                                })
                                .collect(Collectors.toList());

                TouristEstimateResDto res = new TouristEstimateResDto();
                res.setDistance(distanceKm);
                res.setDuration((int) duration);
                res.setGeometry(geometry);
                res.setVehicles(vehiclePricing);

                return res;
        }

        public double calculateScore(Shift shift) {
                // Lấy thời gian hiện tại làm tròn đến giây để tránh lỗi chính xác nano
                LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);

                // Xử lý idle minute của driver: Tìm thời điểm kết thúc chuyến gần nhất (làm
                // tròn đến giây)
                LocalTime lastTripEndTime = shift.getBookingHistories().stream()
                                .filter(r -> r.getAudit().getIsActive())
                                // && r.getServiceRequest().getStatus() == BookingStatus.DONE)
                                .map(BookingHistory::getActualEndTime)
                                .filter(Objects::nonNull)
                                .map(t -> t.truncatedTo(ChronoUnit.SECONDS))
                                .max(Comparator.naturalOrder())
                                .orElse(shift.getStartTime().truncatedTo(ChronoUnit.SECONDS));

                // Tính toán số phút rảnh rỗi dùng Duration để quay lại phong cách ban đầu
                long idleMinutes = Duration.between(lastTripEndTime, now).toMinutes();

                // Tránh giá trị âm nếu có sai lệch thời gian hoặc logic
                if (idleMinutes < 0)
                        idleMinutes = 0;
                double idleScore = Math.min(idleMinutes / 30.0, 1.0);
                // tính qualityscore
                UUID driverId = shift.getDriver().getId();
                List<UUID> serviceRequestsId = requestStatusHistoryRepository
                                .findByDriverIdAndStatus(driverId, BookingStatus.DISPATCHED).stream()
                                .map(rsh -> rsh.getServiceRequest().getId())
                                .collect(Collectors.toList());
                // lấy ra tất cả các request được dispatch cho driver
                long totalDispatchedRequest = serviceRequestsId.size();
                // lấy ra tất cả các request được dispatch cho driver và đã bị cancel bởi
                // tourist
                long totalCancelledRequest = 0;
                if (!serviceRequestsId.isEmpty()) {
                        totalCancelledRequest = requestStatusHistoryRepository
                                        .findByRole("TOURIST").stream()
                                        .filter(rsh -> serviceRequestsId.contains(rsh.getServiceRequest().getId()))
                                        .filter(rsh -> rsh.getServiceRequest().getStatus() == BookingStatus.CANCELLED)
                                        .count();
                }
                // tính tổng số request được giao cho driver
                // bằng cách lấy tổng số request được dispatch cho driver
                // trừ đi số request đã bị cancel bởi tourist
                long totalRequest = totalDispatchedRequest - totalCancelledRequest;
                // tính số request được chấp nhận bởi driver
                long acceptedRequest = requestStatusHistoryRepository
                                .findByActionByUserIdAndStatus(driverId, BookingStatus.ACCEPTED)
                                .stream()
                                .count();
                // tính số request bị canceled bởi driver
                long cancelRequest = requestStatusHistoryRepository
                                .findByActionByUserIdAndStatus(driverId, BookingStatus.CANCELLED)
                                .stream()
                                .count();
                // tính acceptanceRate
                double acceptanceRate = totalRequest > 0 ? (double) acceptedRequest / totalRequest : 0.0;
                // tính cancelRate
                double cancelRate = totalRequest > 0 ? (double) cancelRequest / totalRequest : 0.0;
                // tính qualityScore
                double qualityScore = 0.7 * acceptanceRate + 0.3 * (1 - cancelRate);
                // công thức cuối
                double totalScore = 0.6 * idleScore + 0.4 * qualityScore;
                return totalScore;
        }

        @Override
        @Transactional(readOnly = true)
        public List<MilestoneResDto> getMilestones(UUID bookingId) {
                ServiceRequest requestBooking = serviceRequestRepository.findById(bookingId)
                                .filter(r -> r.getAudit().getIsActive())
                                .orElseThrow(() -> new RuntimeException("Booking not found or inactive"));

                List<MilestoneResDto> milestones = requestBooking.getStatusHistories().stream()
                                .sorted(Comparator.comparing(rsh -> rsh.getAudit().getCreatedAt()))
                                .map(rsh -> MilestoneResDto.builder()
                                                .status(rsh.getStatus())
                                                .audit(MilestoneResDto.Audit.builder()
                                                                .createdAt(rsh.getAudit().getCreatedAt())
                                                                .build())
                                                .build())
                                .collect(Collectors.toList());
                return milestones;
        }

        private LocalDateTime calculateEstimatedEndDate(AirportTransferDetail detail) {
                if (detail == null || detail.getPickupDate() == null || detail.getPickupTime() == null) {
                        return null;
                }
                int duration = estimate(detail.getPickupLat(),
                                detail.getPickupLon(),
                                detail.getDropoffLat(),
                                detail.getDropoffLon());
                LocalDateTime pickupDateTime = LocalDateTime.of(detail.getPickupDate(), detail.getPickupTime());
                return pickupDateTime.plusMinutes(duration);
        }

        private int estimate(double lat1, double lon1, double lat2, double lon2) {
                String url = String.format(Locale.US, "http://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f",
                                lon1, lat1, lon2, lat2);
                Map<?, ?> resp = new RestTemplate().getForObject(url, Map.class);
                List<?> routes = (List<?>) Objects.requireNonNull(resp).get("routes");
                return (int) Math.ceil(((Number) ((Map<?, ?>) routes.get(0)).get("duration")).doubleValue() / 60);
        }

        @Override
        @Transactional
        public void updateRqTourTime(UUID id, LocalTime time) {
                ServiceRequest req = serviceRequestRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Booking not found"));
                if (req.getTourBookingDetail() != null) {
                        req.getTourBookingDetail().setActualPickupTime(time);
                        req.setEstimateEndTime(
                                        calculateEstimatedEndDateForTourBooking(req.getTourBookingDetail(), time));
                }
        }

        private LocalDateTime calculateEstimatedEndDateForTourBooking(TourBookingDetail detail, LocalTime time) {
                // if (detail == null || detail.getPickupDate() == null ||
                // detail.getActualPickupTime() == null) {
                // return null;
                // }
                Long duration = detail.getTour().getDuration();
                LocalDateTime pickupDateTime = LocalDateTime.of(detail.getPickupDate(), time);
                return pickupDateTime.plusMinutes(duration);
        }
}
