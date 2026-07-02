package com.sonnh.bookingcar.data.repository;

import com.sonnh.bookingcar.data.domain.ShiftHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShiftHistoryRepository extends JpaRepository<ShiftHistory, UUID>, JpaSpecificationExecutor<ShiftHistory> {
    
    Optional<ShiftHistory> findFirstByShift_VehicleIdOrderByAudit_CreatedAtDesc(UUID vehicleId);
}
