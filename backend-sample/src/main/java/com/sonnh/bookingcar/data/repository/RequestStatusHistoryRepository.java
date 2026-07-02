package com.sonnh.bookingcar.data.repository;

import com.sonnh.bookingcar.data.domain.RequestStatusHistory;
import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RequestStatusHistoryRepository
        extends JpaRepository<RequestStatusHistory, UUID>, JpaSpecificationExecutor<RequestStatusHistory> {

    @Query("SELECT rsh FROM RequestStatusHistory rsh WHERE rsh.dispatchedDriver.id = :driverId AND rsh.status = :status")
    List<RequestStatusHistory> findByDriverIdAndStatus(@Param("driverId") UUID driverId,
            @Param("status") BookingStatus status);

    @Query("SELECT rsh FROM RequestStatusHistory rsh WHERE rsh.actionBy.id = :userId AND rsh.status = :status")
    List<RequestStatusHistory> findByActionByUserIdAndStatus(@Param("userId") UUID userId,
            @Param("status") BookingStatus status);

    @Query("SELECT rsh FROM RequestStatusHistory rsh WHERE rsh.role = :role")
    List<RequestStatusHistory> findByRole(@Param("role") String role);

    @Query("SELECT rsh FROM RequestStatusHistory rsh WHERE rsh.serviceRequest.id = :requestId AND rsh.status = :status")
    java.util.Optional<RequestStatusHistory> findCancelStatus(@Param("status") BookingStatus status, @Param("requestId") java.util.UUID requestId);
}
