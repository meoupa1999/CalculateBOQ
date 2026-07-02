package com.sonnh.bookingcar.data.repository;

import com.sonnh.bookingcar.data.domain.CheckinTourHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CheckinTourHistoryRepository extends JpaRepository<CheckinTourHistory, UUID>, JpaSpecificationExecutor<CheckinTourHistory> {
    java.util.List<CheckinTourHistory> findByTourId(UUID tourId);
}
