package com.sonnh.bookingcar.service.impl;

import com.sonnh.bookingcar.data.domain.BookingHistory;
import com.sonnh.bookingcar.data.domain.RequestStatusHistory;
import com.sonnh.bookingcar.data.domain.ServiceRequest;
import com.sonnh.bookingcar.data.domain.Shift;
import com.sonnh.bookingcar.data.domain.User;
import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import com.sonnh.bookingcar.data.domain.enums.DriverStatus;
import com.sonnh.bookingcar.data.domain.enums.ShiftHistoryStatus;
import com.sonnh.bookingcar.data.domain.enums.ShiftStatus;
import com.sonnh.bookingcar.data.repository.BookingHistoryRepository;
import com.sonnh.bookingcar.data.repository.RequestStatusHistoryRepository;
import com.sonnh.bookingcar.data.repository.ServiceRequestRepository;
import com.sonnh.bookingcar.data.repository.UserRepository;
import com.sonnh.bookingcar.dto.response.driver.BookingChangeStatusEvent;
import com.sonnh.bookingcar.dto.response.driver.BookingDispatchedAssigned;
import com.sonnh.bookingcar.dto.response.tourist.BookingCancelEvent;
import com.sonnh.bookingcar.mapper.BookingMapper;
import com.sonnh.bookingcar.service.interfaces.DriverBookingService;
import com.sonnh.bookingcar.service.interfaces.ShiftService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverBookingServiceImpl implements DriverBookingService {

        private final ServiceRequestRepository serviceRequestRepository;
        private final BookingHistoryRepository bookingHistoryRepository;
        private final UserRepository userRepository;
        private final RequestStatusHistoryRepository requestStatusHistoryRepository;
        private final ShiftService shiftService;
        private final ApplicationEventPublisher eventPublisher;
        private final BookingMapper bookingMapper;

        @Override
        @Transactional
        public void acceptBooking(UUID id) {
                ServiceRequest req = serviceRequestRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Booking not found"));

                if (req.getStatus() != BookingStatus.DISPATCHED) {
                        throw new RuntimeException("Booking is not in DISPATCHED status and cannot be accepted");
                }

                req.setStatus(BookingStatus.ACCEPTED);
                serviceRequestRepository.save(req);
                saveStatusHistory(req, BookingStatus.ACCEPTED, null);
                eventPublisher.publishEvent(
                                new BookingChangeStatusEvent(bookingMapper.toAdminBookingResDto(req)));
                eventPublisher.publishEvent(
                                new BookingDispatchedAssigned(
                                                bookingMapper.airportDetailToDriverRequestResDto(
                                                                req.getAirportTransferDetail())));
        }

        @Override
        @Transactional
        public void rejectBooking(UUID id, String reasonNote) {
                ServiceRequest req = serviceRequestRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Booking not found"));

                if (req.getStatus() != BookingStatus.DISPATCHED) {
                        throw new RuntimeException("Booking is not in DISPATCHED status and cannot be rejected");
                }

                req.setStatus(BookingStatus.REJECTED_BY_DRIVER);
                serviceRequestRepository.save(req);
                saveStatusHistory(req, BookingStatus.REJECTED_BY_DRIVER, reasonNote);
                eventPublisher.publishEvent(
                                new BookingChangeStatusEvent(bookingMapper.toAdminBookingResDto(req)));
                eventPublisher.publishEvent(
                                new BookingDispatchedAssigned(
                                                bookingMapper.airportDetailToDriverRequestResDto(
                                                                req.getAirportTransferDetail())));
        }

        @Override
        @Transactional
        public void startBooking(UUID id) {
                LocalDateTime pickUpDateTime = null;
                ServiceRequest req = serviceRequestRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Booking not found"));

                if (req.getStatus() != BookingStatus.ACCEPTED) {
                        throw new RuntimeException("Booking is not in ACCEPTED status and cannot be started");
                }
                UUID driverId = null;
                if (req.getAirportTransferDetail() != null) {
                        driverId = req.getAirportTransferDetail().getCurrentShift().getDriver().getId();
                        pickUpDateTime = LocalDateTime.of(req.getAirportTransferDetail().getPickupDate(),
                                        req.getAirportTransferDetail().getPickupTime());
                } else {
                        driverId = req.getTourBookingDetail().getCurrentShift().getDriver().getId();
                        pickUpDateTime = LocalDateTime.of(req.getTourBookingDetail().getPickupDate(),
                                        req.getTourBookingDetail().getActualPickupTime());
                }
                if (serviceRequestRepository.countByDriverIdAndStatus(driverId,
                                BookingStatus.RUNNING) > 0) {
                        throw new RuntimeException("You have a booking in RUNNING status");
                }

                // check xem thời gian có > 2 tiếng không
                if (!LocalDateTime.now().isBefore(pickUpDateTime.minusHours(2))
                                && !LocalDateTime.now().isAfter(pickUpDateTime.minusHours(2))) {
                        req.setStatus(BookingStatus.WARNING_RUNNING);
                        throw new RuntimeException("Running time is not within 2 hours of pickup time");
                } else {
                        //
                        req.setStatus(BookingStatus.RUNNING);
                        User driver = userRepository.findById(driverId)
                                        .orElseThrow(() -> new RuntimeException("Driver not found"));
                        driver.setDriverStatus(DriverStatus.BUSY);
                        userRepository.save(driver);
                        serviceRequestRepository.save(req);
                        saveStatusHistory(req, BookingStatus.RUNNING, null);
                }
                eventPublisher.publishEvent(
                                new BookingChangeStatusEvent(bookingMapper.toAdminBookingResDto(req)));
                eventPublisher.publishEvent(
                                new BookingDispatchedAssigned(
                                                bookingMapper.airportDetailToDriverRequestResDto(
                                                                req.getAirportTransferDetail())));
        }

        @Override
        @Transactional
        public void cancelBooking(UUID id, String reasonNote) {
                ServiceRequest req = serviceRequestRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Booking not found"));

                if (req.getStatus() == BookingStatus.DISPATCHED) {
                        throw new RuntimeException(
                                        "Booking is in DISPATCHED status. Please use reject booking instead of cancel.");
                }

                EnumSet<BookingStatus> nonCancellable = EnumSet.of(
                                BookingStatus.DONE,
                                BookingStatus.CANCELLED,
                                BookingStatus.CANCELED_BY_DRIVER,
                                BookingStatus.CANCELED_BY_ADMIN,
                                BookingStatus.REJECTED_BY_DRIVER);

                if (nonCancellable.contains(req.getStatus())) {
                        throw new RuntimeException("Booking in status " + req.getStatus() + " cannot be canceled.");
                }

                req.setStatus(BookingStatus.CANCELED_BY_DRIVER);
                serviceRequestRepository.save(req);
                saveStatusHistory(req, BookingStatus.CANCELED_BY_DRIVER, reasonNote);
                eventPublisher.publishEvent(
                                new BookingCancelEvent(bookingMapper.toAdminBookingResDto(req)));
                eventPublisher.publishEvent(
                                new BookingDispatchedAssigned(
                                                bookingMapper.airportDetailToDriverRequestResDto(
                                                                req.getAirportTransferDetail())));
        }

        @Override
        @Transactional
        public void completeBooking(UUID id) {
                log.info("Starting completeBooking for ID: {}", id);
                ServiceRequest req = serviceRequestRepository.findById(id)
                                .orElseThrow(() -> {
                                        log.error("Booking not found for ID: {}", id);
                                        return new RuntimeException("Booking not found");
                                });

                if (req.getStatus() != BookingStatus.RUNNING) {
                        log.warn("Booking is not in RUNNING status. Current status: {}. ID: {}", req.getStatus(), id);
                        throw new RuntimeException("Booking is not in RUNNING status and cannot be completed");
                }

                Shift currentShift = getShiftFromRequest(req);

                if (currentShift == null) {
                        log.error("No shift assigned to this booking. ID: {}", id);
                        throw new RuntimeException("No shift assigned to this booking");
                }

                log.info("Found shift for booking complete. Shift ID: {}", currentShift.getId());

                // Update status to DONE
                UUID driverId = null;
                if (req.getAirportTransferDetail() != null) {
                        driverId = req.getAirportTransferDetail().getCurrentShift().getDriver().getId();
                } else {
                        driverId = req.getTourBookingDetail().getCurrentShift().getDriver().getId();
                }
                if (serviceRequestRepository.countByDriverIdAndStatus(driverId, BookingStatus.RUNNING) < 2) {
                        User driver = userRepository.findById(driverId)
                                        .orElseThrow(() -> new RuntimeException("Driver not found"));
                        driver.setDriverStatus(DriverStatus.AVAILABLE);
                        userRepository.save(driver);
                }

                req.setStatus(BookingStatus.DONE);
                serviceRequestRepository.save(req);
                saveStatusHistory(req, BookingStatus.DONE, null);

                // Save Snapshot
                BookingHistory snapshot = req.getHistory();
                if (snapshot == null) {
                        snapshot = new BookingHistory();
                        snapshot.addServiceRequest(req);
                }

                snapshot.addShift(currentShift);
                snapshot.setDriverNameSnapshot(
                                currentShift.getDriver() != null ? currentShift.getDriver().getFullName() : "N/A");
                snapshot.setVehiclePlateSnapshot(
                                currentShift.getVehicle() != null ? currentShift.getVehicle().getPlateNumber() : "N/A");
                snapshot.setVehicleModelSnapshot(
                                currentShift.getVehicle() != null ? currentShift.getVehicle().getModel() : "N/A");
                snapshot.setActualEndTime(java.time.LocalTime.now());
                snapshot.setStatusSnapshot("DONE");

                bookingHistoryRepository.save(snapshot);

                // Logic to close shift if it's in OVERTIME and this was the last booking
                if (currentShift.getStatus() == ShiftStatus.OVERTIME) {
                        boolean hasOtherActive = shiftService.hasActiveBookings(currentShift);
                        if (!hasOtherActive) {
                                shiftService.finalizeShift(currentShift, ShiftHistoryStatus.NORMAL);
                        }
                }
                eventPublisher.publishEvent(
                                new BookingChangeStatusEvent(bookingMapper.toAdminBookingResDto(req)));
                eventPublisher.publishEvent(
                                new BookingDispatchedAssigned(
                                                bookingMapper.airportDetailToDriverRequestResDto(
                                                                req.getAirportTransferDetail())));
        }

        private void saveStatusHistory(ServiceRequest req, BookingStatus status, String reasonNote) {
                Shift currentShift = getShiftFromRequest(req);

                RequestStatusHistory history = RequestStatusHistory.builder()
                                .status(status)
                                .role("DRIVER")
                                .reasonNote(reasonNote)
                                .build();

                history.addServiceRequest(req);
                if (currentShift != null && currentShift.getDriver() != null) {
                        history.addActionBy(currentShift.getDriver());
                }

                requestStatusHistoryRepository.save(history);
        }

        private Shift getShiftFromRequest(ServiceRequest req) {
                if (req.getAirportTransferDetail() != null) {
                        return req.getAirportTransferDetail().getCurrentShift();
                } else if (req.getTourBookingDetail() != null) {
                        return req.getTourBookingDetail().getCurrentShift();
                }
                return null;
        }
}
