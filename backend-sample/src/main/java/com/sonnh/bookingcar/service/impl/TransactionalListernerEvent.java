package com.sonnh.bookingcar.service.impl;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import com.sonnh.bookingcar.dto.response.admin.AdminBookingResDto;
import com.sonnh.bookingcar.dto.response.driver.BookingChangeStatusEvent;
import com.sonnh.bookingcar.dto.response.driver.BookingCompleteEvent;
import com.sonnh.bookingcar.dto.response.driver.BookingDispatchedAssigned;
import com.sonnh.bookingcar.dto.response.driver.DriverRequestResDto;
import com.sonnh.bookingcar.dto.response.tourist.BookingCancelEvent;
import com.sonnh.bookingcar.dto.response.tourist.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionalListernerEvent {
    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookingCreated(BookingCreatedEvent event) {

        AdminBookingResDto dto = event.getBookingRequesDto();

        System.out.println("WebSocket: Sending new booking to /topic/bookings: " + dto.getBookingCode());

        // push realtime cho admin
        messagingTemplate.convertAndSend(
                "/topic/bookings",
                dto);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookingCancel(BookingCancelEvent event) {

        AdminBookingResDto dto = event.getBookingResponseDto();

        System.out.println("WebSocket: Sending new booking to /topic/bookings: " + dto.getBookingCode());

        // push realtime cho admin
        messagingTemplate.convertAndSend(
                "/topic/bookings",
                dto);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookingChangeStatus(BookingChangeStatusEvent event) {
        AdminBookingResDto dto = event.getBookingResponseDto();

        System.out.println("WebSocket: Sending new booking to /topic/bookings: " + dto.getBookingCode());

        // push realtime cho admin
        messagingTemplate.convertAndSend(
                "/topic/bookings",
                dto);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookingDispatchedAssigned(BookingDispatchedAssigned event) {
        DriverRequestResDto dto = event.getAssignedRequests();

        System.out.println("WebSocket: Sending new booking to /topic/bookings: " + dto.getBookingCode());

        // push realtime cho admin
        messagingTemplate.convertAndSend(
                "/topic/driver-bookings",
                dto);
    }

    // @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    // public void handleCompleteBooking(BookingDispatchedAssigned event) {
    // DriverRequestResDto dto = event.getAssignedRequests();

    // System.out.println("WebSocket: Sending new booking to /topic/bookings: " +
    // dto.getBookingCode());

    // // push realtime cho admin
    // messagingTemplate.convertAndSend(
    // "/topic/driver-bookings",
    // dto);
    // }

    // @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    // public void handleCompleteBooking(BookingCompleteEvent event) {
    // DriverRequestResDto dto = event.getDriverRequestResDto();

    // System.out.println("WebSocket: Sending new booking to /topic/bookings: " +
    // dto.getBookingCode());

    // // push realtime cho admin
    // messagingTemplate.convertAndSend(
    // "/topic/driver-bookings",
    // dto);
    // }

}
