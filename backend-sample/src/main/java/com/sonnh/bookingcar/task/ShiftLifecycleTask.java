// package com.sonnh.bookingcar.task;

// import com.sonnh.bookingcar.data.domain.Shift;
// import com.sonnh.bookingcar.data.domain.ShiftHistory;
// import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
// import com.sonnh.bookingcar.data.domain.enums.ShiftHistoryStatus;
// import com.sonnh.bookingcar.data.domain.enums.ShiftStatus;
// import com.sonnh.bookingcar.data.domain.enums.VehicleStatus;
// import com.sonnh.bookingcar.data.repository.ShiftRepository;
// import com.sonnh.bookingcar.data.repository.ServiceRequestRepository;
// import com.sonnh.bookingcar.data.domain.ServiceRequest;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDate;
// import java.time.LocalTime;
// import java.util.List;

// @Component
// @RequiredArgsConstructor
// @Slf4j
// public class ShiftLifecycleTask {

// private final ShiftRepository shiftRepository;
// private final ServiceRequestRepository serviceRequestRepository;

// @Scheduled(cron = "0 * * * * *") // Every minute
// @Transactional
// public void processShiftLifecycle() {
// LocalDate today = LocalDate.now();
// LocalTime nowTime = LocalTime.now();

// // 1. Open Shifts: CLOSED -> OPENED
// // Logic: CLOSED status AND within date range AND within time range
// List<Shift> shiftsToOpen =
// shiftRepository.findAllByStatusAndDateAndTimeRange(
// ShiftStatus.CLOSED, today, nowTime);

// for (Shift shift : shiftsToOpen) {
// log.info("Opening shift: {} for driver: {}", shift.getId(),
// shift.getDriver().getFullName());
// shift.setStatus(ShiftStatus.OPENED);
// // We don't create history here, it's created when driver clicks "Start
// Shift"
// }

// // 2. Close or Overtime: shiftsToClose
// List<Shift> shiftsPastTime = shiftRepository.findAllActivePastEndTime(today,
// nowTime);

// // 2a. Set OVERTIME for shifts with running bookings
// shiftsPastTime.stream()
// .filter(s -> (s.getAirportTransferDetails() != null &&
// s.getAirportTransferDetails().stream().anyMatch(d -> d.getServiceRequest() !=
// null && d.getServiceRequest().getStatus() == BookingStatus.RUNNING)) ||
// (s.getTourBookingDetails() != null &&
// s.getTourBookingDetails().stream().anyMatch(d -> d.getServiceRequest() !=
// null && d.getServiceRequest().getStatus() == BookingStatus.RUNNING)))
// .filter(s -> s.getStatus() != ShiftStatus.OVERTIME)
// .forEach(s -> {
// log.info("Shift {} entering OVERTIME", s.getId());
// s.setStatus(ShiftStatus.OVERTIME);
// shiftRepository.save(s);
// });

// // 2b. Finalize/Close shifts with NO running bookings
// shiftsPastTime.stream()
// .filter(s -> !((s.getAirportTransferDetails() != null &&
// s.getAirportTransferDetails().stream().anyMatch(d -> d.getServiceRequest() !=
// null && d.getServiceRequest().getStatus() == BookingStatus.RUNNING)) ||
// (s.getTourBookingDetails() != null &&
// s.getTourBookingDetails().stream().anyMatch(d -> d.getServiceRequest() !=
// null && d.getServiceRequest().getStatus() == BookingStatus.RUNNING))))
// .forEach(s -> {
// log.info("Closing shift: {} (past end time)", s.getId());

// // Recall unfinished bookings before closing
// recallUnfinishedBookings(s);

// s.setStatus(ShiftStatus.CLOSED);
// if (s.getVehicle() != null)
// s.getVehicle().setStatus(VehicleStatus.AVAILABLE);
// s.getShiftHistories().stream()
// .filter(ShiftHistory::isProcessing)
// .findFirst()
// .ifPresent(h -> {
// h.setActualEndTime(LocalTime.now());
// h.setFinalStatus(ShiftHistoryStatus.NORMAL);
// h.setProcessing(false);
// });
// shiftRepository.save(s);
// });
// }

// private void recallUnfinishedBookings(Shift shift) {
// if (shift.getAirportTransferDetails() != null) {
// shift.getAirportTransferDetails().forEach(detail -> {
// ServiceRequest req =
// detail.getServiceRequest();
// if (req != null && (req.getStatus() == BookingStatus.ACCEPTED ||
// req.getStatus() == BookingStatus.DISPATCHED)) {
// log.info("Recalling Airport Transfer {} from Shift {} to WAITING",
// req.getId(), shift.getId());
// req.setStatus(BookingStatus.WAITING);
// detail.setCurrentShift(null);
// serviceRequestRepository.save(req);
// }
// });
// }
// if (shift.getTourBookingDetails() != null) {
// shift.getTourBookingDetails().forEach(detail -> {
// ServiceRequest req =
// detail.getServiceRequest();
// if (req != null && (req.getStatus() == BookingStatus.ACCEPTED ||
// req.getStatus() == BookingStatus.DISPATCHED)) {
// log.info("Recalling Tour Booking {} from Shift {} to WAITING", req.getId(),
// shift.getId());
// req.setStatus(BookingStatus.WAITING);
// detail.setCurrentShift(null);
// serviceRequestRepository.save(req);
// }
// });
// }
// }
// }
