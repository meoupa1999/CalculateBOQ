package com.sonnh.bookingcar.data.repository;

import com.sonnh.bookingcar.data.domain.ServiceRequest;
import com.sonnh.bookingcar.data.domain.User;
import com.sonnh.bookingcar.data.domain.enums.BookingStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ServiceRequestRepository
                extends JpaRepository<ServiceRequest, UUID>, JpaSpecificationExecutor<ServiceRequest> {

        @Query("SELECT sr FROM ServiceRequest sr WHERE sr.tourist = :tourist")
        Page<ServiceRequest> findByTourist(@Param("tourist") User tourist, Pageable pageable);

        @Query("SELECT sr FROM ServiceRequest sr WHERE sr.tourist = :tourist AND sr.audit.isActive = true")
        Page<ServiceRequest> findByTouristAndAuditIsActiveTrue(@Param("tourist") User tourist, Pageable pageable);

        @Query("SELECT count(sr) FROM ServiceRequest sr WHERE sr.status = :status AND sr.audit.isActive = true")
        Long countByStatus(@Param("status") com.sonnh.bookingcar.data.domain.enums.BookingStatus status);

        @Query("SELECT count(sr) FROM ServiceRequest sr " +
                        "WHERE (sr.status = 'DISPATCHED' " +
                        "OR sr.status = 'ACCEPTED' " +
                        "OR sr.status = 'RUNNING') " +
                        "AND sr.audit.isActive = true")
        Long countByProcessingStatus();

        @Query("SELECT count(sr) FROM ServiceRequest sr WHERE sr.audit.isActive = true")
        Long countAllActiveStatus();

        java.util.List<ServiceRequest> findByStatusAndAuditCreatedAtBetween(
                        com.sonnh.bookingcar.data.domain.enums.BookingStatus status,
                        java.time.LocalDateTime start,
                        java.time.LocalDateTime end);

        @Query("SELECT sr " +
                        "FROM ServiceRequest sr " +
                        "LEFT JOIN AirportTransferDetail atd ON sr.id = atd.serviceRequest.id " +
                        "LEFT JOIN TourBookingDetail tbd ON sr.id = tbd.serviceRequest.id " +
                        "LEFT JOIN Shift s1 ON atd.currentShift.id = s1.id " +
                        "LEFT JOIN Shift s2 ON tbd.currentShift.id = s2.id " +
                        "WHERE sr.audit.isActive = true " +
                        "AND sr.status IN :statuses " +
                        "AND (atd IS NOT NULL AND atd.pickupDate BETWEEN :startDate AND :endDate " +
                        "OR tbd IS NOT NULL AND tbd.pickupDate BETWEEN :startDate AND :endDate) " +
                        "AND (s1.driver.id = :driverId or s2.driver.id = :driverId)")
        List<ServiceRequest> getInRangeDate(
                        LocalDate startDate,
                        LocalDate endDate,
                        Set<BookingStatus> statuses,
                        UUID driverId);

        @Query("SELECT count(sr) " +
                        "FROM ServiceRequest sr " +
                        "LEFT JOIN sr.airportTransferDetail atd " +
                        "LEFT JOIN sr.tourBookingDetail tbd " +
                        "LEFT JOIN atd.currentShift s1 " +
                        "LEFT JOIN tbd.currentShift s2 " +
                        "LEFT JOIN s1.driver u1 " +
                        "LEFT JOIN s2.driver u2 " +
                        "WHERE sr.audit.isActive = true " +
                        "AND ((u1 IS NOT NULL AND u1.id = :driverId) OR (u2 IS NOT NULL AND u2.id = :driverId)) " +
                        "AND sr.status = :status")
        Long countByDriverIdAndStatus(UUID driverId, BookingStatus status);
}