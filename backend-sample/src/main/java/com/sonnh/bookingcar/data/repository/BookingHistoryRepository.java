package com.sonnh.bookingcar.data.repository;

import com.sonnh.bookingcar.data.domain.BookingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookingHistoryRepository extends JpaRepository<BookingHistory, UUID> {
    // @Query("SELECT bh FROM BookingHistory bh JOIN Shift s ON bh.shift.id = s.id
    // JOIN Users u ON s.user.id = u.id WHERE u.id = :userId")
    // List<BookingHistory> findByUserId(UUID userId);
}
