package com.sonnh.bookingcar.service.interfaces;

import java.util.UUID;

public interface DriverBookingService {
    void acceptBooking(UUID id);
    void rejectBooking(UUID id, String reasonNote);
    void startBooking(UUID id);
    void completeBooking(UUID id);
    void cancelBooking(UUID id, String reasonNote);
}
