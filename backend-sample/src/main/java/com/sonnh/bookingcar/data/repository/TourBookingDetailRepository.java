package com.sonnh.bookingcar.data.repository;

import com.sonnh.bookingcar.data.domain.TourBookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TourBookingDetailRepository extends JpaRepository<TourBookingDetail, UUID> {
    
    @Query("SELECT tbd FROM TourBookingDetail tbd WHERE tbd.serviceRequest.id = :requestId")
    Optional<TourBookingDetail> findByServiceRequestId(@Param("requestId") UUID requestId);
}
