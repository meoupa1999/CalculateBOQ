package com.sonnh.bookingcar.service.impl;

import com.sonnh.bookingcar.data.domain.AirportTransferDetail;
import com.sonnh.bookingcar.data.domain.Notification;
import com.sonnh.bookingcar.data.domain.ServiceRequest;
import com.sonnh.bookingcar.data.domain.User;
import com.sonnh.bookingcar.data.domain.VehicleType;
import com.sonnh.bookingcar.data.domain.VehiclesTypePrice;
import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import com.sonnh.bookingcar.data.domain.enums.ServiceType;
import com.sonnh.bookingcar.data.repository.AirportTransferDetailRepository;
import com.sonnh.bookingcar.data.repository.NotificationRepository;
import com.sonnh.bookingcar.data.repository.ServiceRequestRepository;
import com.sonnh.bookingcar.data.repository.UserRepository;
import com.sonnh.bookingcar.data.repository.VehicleTypeRepository;
import com.sonnh.bookingcar.data.domain.RequestStatusHistory;
import com.sonnh.bookingcar.data.domain.Tour;
import com.sonnh.bookingcar.data.domain.TourBookingDetail;
import com.sonnh.bookingcar.data.repository.RequestStatusHistoryRepository;
import com.sonnh.bookingcar.data.repository.TourBookingDetailRepository;
import com.sonnh.bookingcar.data.repository.TourRepository;
import com.sonnh.bookingcar.data.repository.VehiclesTypePriceRepository;
import com.sonnh.bookingcar.data.specification.ServiceRequestSpecification;
import com.sonnh.bookingcar.dto.NotificationPushDto;
import com.sonnh.bookingcar.dto.request.tourist.AirportTransferReqDto;
import com.sonnh.bookingcar.dto.request.tourist.TourBookingReqDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.admin.AdminNotificationEvent;
import com.sonnh.bookingcar.dto.response.tourist.BookingCancelEvent;
import com.sonnh.bookingcar.dto.response.tourist.BookingCreatedEvent;
import com.sonnh.bookingcar.dto.response.tourist.TouristBookingDetailResDto;
import com.sonnh.bookingcar.dto.response.tourist.TouristBookingResDto;
import com.sonnh.bookingcar.exception.ResourceNotFoundException;
import com.sonnh.bookingcar.mapper.BookingMapper;
import com.sonnh.bookingcar.mapper.VehicleTypeMapper;
import com.sonnh.bookingcar.security.SecurityUtils;
import com.sonnh.bookingcar.service.interfaces.TouristRequestService;
import com.sonnh.bookingcar.dto.request.tourist.TripEstimateReqDto;
import com.sonnh.bookingcar.dto.response.tourist.TouristEstimateResDto;
import org.springframework.data.jpa.domain.Specification;
import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TouristRequestServiceImpl implements TouristRequestService {

        private final ServiceRequestRepository serviceRequestRepository;
        private final AirportTransferDetailRepository airportTransferDetailRepository;
        private final TourBookingDetailRepository tourBookingDetailRepository;
        private final TourRepository tourRepository;
        private final VehiclesTypePriceRepository vehiclesTypePriceRepository;
        private final UserRepository userRepository;
        private final VehicleTypeRepository vehicleTypeRepository;
        private final RequestStatusHistoryRepository requestStatusHistoryRepository;
        private final NotificationRepository notificationRepository;
        private final BookingMapper bookingMapper;
        private final VehicleTypeMapper vehicleTypeMapper;
        private final ApplicationEventPublisher eventPublisher;

        @Override
        @Transactional
        public UUID createAirportTransfer(AirportTransferReqDto dto) {
                User tourist = userRepository.findById(SecurityUtils.getCurrentUserId())
                                .filter(u -> u.getRole().getName().equals("TOURIST"))
                                .orElseThrow(() -> new ResourceNotFoundException("No tourist found in system"));

                ServiceRequest request = bookingMapper.toServiceRequest(dto);
                request.addTourist(tourist);

                AirportTransferDetail detail = bookingMapper.toAirportTransferDetail(dto);

                //
                request.setEstimateEndTime(calculateEstimatedEndDateForAirportTransfer(detail));
                //

                // Use the synchronization method
                detail.addServiceRequest(request);

                // thêm vehicle type vào
                VehicleType vehicleType = vehicleTypeRepository.findById(dto.getVehicleTypeId())
                                .orElseThrow(() -> new ResourceNotFoundException("Vehicle type not found"));
                detail.addVehicleType(vehicleType);
                detail.setVehicleTypeName(vehicleType.getName());
                serviceRequestRepository.save(request);
                airportTransferDetailRepository.save(detail);

                // Publish event for real-time update
                eventPublisher.publishEvent(
                                BookingCreatedEvent.builder()
                                                .bookingRequesDto(bookingMapper.toAdminBookingResDto(request))
                                                .build());

                //
                // xử lý bắn notification cho admin
                UUID notificationId = saveNotification(tourist, request, dto, null);
                eventPublisher.publishEvent(
                                new AdminNotificationEvent(NotificationPushDto.builder()
                                                .notificationId(notificationId)
                                                .requestId(request.getId())
                                                .title("Đơn đặt đưa đón sân bay mới")
                                                .message("Khách hàng: " + tourist.getFullName() + " vừa đặt xe đi từ "
                                                                + dto.getShortPickupLocation() + " đến "
                                                                + dto.getShortDropoffLocation() + " vào lúc "
                                                                + dto.getPickupTime() + " ngày " + dto.getPickupDate())
                                                .type("BOOKING")
                                                .timestamp(request.getAudit().getCreatedAt())
                                                .actionUrl(String.format("/bookings/%s/dispatch/airport",
                                                                request.getId()))
                                                .build()));

                return request.getId();
        }

        @Override
        @Transactional
        public UUID createTourBooking(TourBookingReqDto dto) {
                User tourist = userRepository.findById(SecurityUtils.getCurrentUserId())
                                .filter(u -> u.getRole().getName().equals("TOURIST"))
                                .orElseThrow(() -> new ResourceNotFoundException("No tourist found in system"));

                Tour tour = tourRepository.findById(dto.getTourId())
                                .orElseThrow(() -> new ResourceNotFoundException("Tour not found"));

                ServiceRequest request = bookingMapper.toServiceRequest(dto);
                request.addTourist(tourist);
                serviceRequestRepository.saveAndFlush(request);

                TourBookingDetail detail = bookingMapper.toTourBookingDetail(dto);
                detail.setActualPickupTime(tour.getDefaultPickupTime());
                detail.addTour(tour);
                detail.addServiceRequest(request);

                if (dto.getVehiclePriceId() != null) {
                        VehiclesTypePrice vp = vehiclesTypePriceRepository
                                        .findById(dto.getVehiclePriceId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Vehicle price not found"));
                        detail.addVehicleTypePrice(vp);
                }

                tourBookingDetailRepository.save(detail);

                // update end date
                LocalDateTime estimatedEndDate = calculateEstimatedEndDateForTourBooking(detail);
                request.setEstimateEndTime(estimatedEndDate);
                // serviceRequestRepository.save(request);

                // xử lý phần transactional event
                eventPublisher.publishEvent(
                                new BookingCreatedEvent(bookingMapper.toAdminBookingResDto(request)));
                //
                // xử lý bắn notification cho admin
                UUID notificationId = saveNotification(tourist, request, null, tour);
                eventPublisher.publishEvent(
                                new AdminNotificationEvent(NotificationPushDto.builder()
                                                .notificationId(notificationId)
                                                .requestId(request.getId())
                                                .title("Đơn tour mới")
                                                .message("Khách hàng: " + tourist.getFullName() + " vừa đặt tour "
                                                                + tour.getName())
                                                .type("BOOKING")
                                                .timestamp(request.getAudit().getCreatedAt())
                                                .actionUrl(String.format("/bookings/%s/dispatch/tour",
                                                                request.getId()))
                                                .build()));

                return request.getId();
        }

        @Override
        @Transactional
        public void cancelBooking(UUID id, String reasonNote) {
                ServiceRequest req = serviceRequestRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

                EnumSet<BookingStatus> cancellableStatuses = EnumSet.of(
                                BookingStatus.WAITING,
                                BookingStatus.DISPATCHED,
                                BookingStatus.REJECTED_BY_DRIVER,
                                BookingStatus.RUNNING,
                                BookingStatus.ACCEPTED);

                if (!cancellableStatuses.contains(req.getStatus())) {
                        throw new RuntimeException("Booking in status " + req.getStatus() + " cannot be cancelled");
                }

                req.setStatus(BookingStatus.CANCELLED);
                serviceRequestRepository.save(req);

                // Record history
                RequestStatusHistory history = RequestStatusHistory.builder()
                                .status(BookingStatus.CANCELLED)
                                .role("TOURIST")
                                .reasonNote(reasonNote)
                                .build();
                history.addServiceRequest(req);
                if (req.getTourist() != null) {
                        history.addActionBy(req.getTourist());
                }
                requestStatusHistoryRepository.save(history);
                // xử lý phần transactional event
                eventPublisher.publishEvent(
                                new BookingCancelEvent(bookingMapper.toAdminBookingResDto(req)));

        }

        @Override
        @Transactional(readOnly = true)
        public PageImplResDto<TouristBookingResDto> getTouristBookings(String searchKeyword, BookingStatus status,
                        ServiceType type, Integer page, Integer size) {
                User tourist = userRepository.findById(SecurityUtils.getCurrentUserId())
                                .orElseThrow(() -> new ResourceNotFoundException("No tourist found in system"));

                Pageable pageable = PageRequest.of(Math.max(0, page - 1), size,
                                Sort.by("audit.updatedAt").descending());

                Specification<ServiceRequest> spec = Specification
                                .where(ServiceRequestSpecification.hasTourist(tourist))
                                .and(ServiceRequestSpecification.isActive());

                if (searchKeyword != null && !searchKeyword.isEmpty()) {
                        spec = spec.and(ServiceRequestSpecification.search(searchKeyword));
                }

                if (status != null) {
                        spec = spec.and(ServiceRequestSpecification.hasStatus(status));
                }

                if (type != null) {
                        spec = spec.and(ServiceRequestSpecification.hasType(type));
                }

                Page<ServiceRequest> pageResult = serviceRequestRepository.findAll(spec, pageable);
                Page<TouristBookingResDto> dtoPage = pageResult.map(bookingMapper::toTouristBookingResDto);

                return PageImplResDto.fromPage(dtoPage);
        }

        @Override
        @Transactional(readOnly = true)
        public TouristBookingDetailResDto getBookingDetail(UUID id) {
                ServiceRequest req = serviceRequestRepository.findById(id)
                                .filter(r -> r.getAudit().getIsActive())
                                .orElseThrow(() -> new ResourceNotFoundException("Booking not found or inactive"));

                // Verify it belongs to current tourist (temporary logic)
                User tourist = userRepository.findAll().stream()
                                .filter(u -> u.getAudit().getIsActive())
                                .filter(u -> u.getRole().getName().equals("TOURIST"))
                                .findFirst()
                                .orElseThrow(() -> new ResourceNotFoundException("No tourist found in system"));

                if (!req.getTourist().getId().equals(tourist.getId())) {
                        throw new RuntimeException("You are not authorized to view this booking");
                }

                TouristBookingDetailResDto dto = bookingMapper.toTouristBookingDetailResDto(req);

                java.util.List<BookingStatus> cancelStatuses = java.util.List.of(
                                BookingStatus.CANCELLED,
                                BookingStatus.CANCELED_BY_ADMIN,
                                BookingStatus.CANCELED_BY_DRIVER,
                                BookingStatus.REJECTED_BY_DRIVER);

                if (cancelStatuses.contains(req.getStatus())) {
                        requestStatusHistoryRepository.findCancelStatus(req.getStatus(), req.getId())
                                        .ifPresent(h -> {
                                                dto.setCancelReason(TouristBookingDetailResDto.CancelReasonResDto
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
                /*
                 * // GOOGLE MAPS IMPLEMENTATION (TEMPORARILY COMMENTED OUT)
                 * if (request.getDistance() == null || request.getDistance() <= 0) {
                 * throw new RuntimeException("Invalid distance");
                 * }
                 * 
                 * if (request.getDuration() == null || request.getDuration() <= 0) {
                 * throw new RuntimeException("Invalid duration");
                 * }
                 * 
                 * double distanceKm = request.getDistance() / 1000;
                 * int durationMin = (int) Math.ceil(request.getDuration() / 60);
                 * String geometry = null;
                 */

                // OPENSTREETMAP (OSRM) IMPLEMENTATION
                if (request.getPickupLat() == null || request.getPickupLon() == null ||
                                request.getDestLat() == null || request.getDestLon() == null) {
                        throw new RuntimeException("Missing coordinates for OSRM estimation");
                }

                Map<String, Object> route = this.getRoute(
                                request.getPickupLat(),
                                request.getPickupLon(),
                                request.getDestLat(),
                                request.getDestLon());

                double distanceKm = ((Number) route.get("distance")).doubleValue() / 1000;
                int durationMin = (int) Math.ceil(((Number) route.get("duration")).doubleValue() / 60);
                String geometry = (String) route.get("geometry");

                List<TouristEstimateResDto.VehiclesPricingDto> vehiclePricing = vehicleTypeRepository.findAll().stream()
                                .filter(vt -> vt.getAudit().getIsActive())
                                .filter(vt -> vt.getIsDistanceBookingEnabled())
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
                // tính vận tốc trung bình
                int averageSpeed = (int) Math.ceil((distanceKm) / (durationMin / 60.0));

                TouristEstimateResDto res = new TouristEstimateResDto();
                res.setDistance(distanceKm);
                res.setDuration(durationMin);
                res.setGeometry(geometry);
                res.setAverageSpeed(averageSpeed);
                res.setVehicles(vehiclePricing);

                return res;
        }

        private LocalDateTime calculateEstimatedEndDateForAirportTransfer(AirportTransferDetail detail) {
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

        private LocalDateTime calculateEstimatedEndDateForTourBooking(TourBookingDetail detail) {
                if (detail == null || detail.getPickupDate() == null || detail.getActualPickupTime() == null) {
                        return null;
                }
                Long duration = detail.getTour().getDuration();
                LocalDateTime pickupDateTime = LocalDateTime.of(detail.getPickupDate(), detail.getActualPickupTime());
                return pickupDateTime.plusMinutes(duration);
        }

        private int estimate(double lat1, double lon1, double lat2, double lon2) {
                String url = String.format(Locale.US, "http://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f",
                                lon1, lat1, lon2, lat2);
                Map<?, ?> resp = new RestTemplate().getForObject(url, Map.class);
                List<?> routes = (List<?>) Objects.requireNonNull(resp).get("routes");
                return (int) Math.ceil(((Number) ((Map<?, ?>) routes.get(0)).get("duration")).doubleValue() / 60);
        }

        private UUID saveNotification(User tourist, ServiceRequest request, AirportTransferReqDto airportDto,
                        Tour tour) {
                if (airportDto != null) {
                        Notification notification = notificationRepository.save(Notification.builder()
                                        .requestId(request.getId())
                                        .title("Đơn đặt đưa đón sân bay mới")
                                        .message("Khách hàng: " + tourist.getFullName() + " vừa đặt xe đi từ "
                                                        + airportDto.getShortPickupLocation() + " đến "
                                                        + airportDto.getShortDropoffLocation() + " vào lúc "
                                                        + airportDto.getPickupTime() + " ngày "
                                                        + airportDto.getPickupDate())
                                        .type("BOOKING")
                                        .timestamp(request.getAudit().getCreatedAt())
                                        .actionUrl(String.format("/bookings/%s/dispatch/airport",
                                                        request.getId()))
                                        .build());
                        return notification.getId();
                } else {
                        Notification notification = notificationRepository.save(Notification.builder()
                                        .requestId(request.getId())
                                        .title("Đơn tour mới")
                                        .message("Khách hàng: " + tourist.getFullName() + " vừa đặt tour "
                                                        + tour.getName())
                                        .type("BOOKING")
                                        .timestamp(request.getAudit().getCreatedAt())
                                        .actionUrl(String.format("/bookings/%s/dispatch/tour",
                                                        request.getId()))
                                        .build());
                        return notification.getId();
                }
        }
}
