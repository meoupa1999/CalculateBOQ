package com.sonnh.bookingcar.data.repository;

import com.sonnh.bookingcar.data.domain.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import com.sonnh.bookingcar.data.domain.enums.ShiftStatus;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID>, JpaSpecificationExecutor<Shift> {
       @EntityGraph(attributePaths = { "shiftHistories" })
       @Query("SELECT s FROM Shift s WHERE s.driver.id = :driverId")
       List<Shift> findByDriverId(@Param("driverId") UUID driverId);

       @Query("SELECT s FROM Shift s " +
                     "LEFT JOIN FETCH s.airportTransferDetails d " +
                     "LEFT JOIN FETCH d.serviceRequest " +
                     "WHERE s.driver.id = :driverId")
       List<Shift> findWithAirportDetailsByDriverId(@Param("driverId") UUID driverId);

       @EntityGraph(attributePaths = { "tourBookingDetails.serviceRequest" })
       @Query("SELECT s FROM Shift s WHERE s.driver.id = :driverId")
       List<Shift> findWithTourDetailsByDriverId(@Param("driverId") UUID driverId);

       @Query("SELECT s FROM Shift s WHERE s.status = :status " +
                     "AND s.startDate <= :date AND s.endDate >= :date " +
                     "AND s.startTime <= :time AND s.endTime > :time")
       List<Shift> findAllByStatusAndDateAndTimeRange(
                     @Param("status") ShiftStatus status,
                     @Param("date") LocalDate date,
                     @Param("time") LocalTime time);

       @Query("SELECT s FROM Shift s WHERE (s.status = 'OPENED' OR s.status = 'CLOSING' OR s.status = 'OVERTIME') " +
                     "AND (s.endDate < :date OR (s.endDate = :date AND s.endTime <= :time))")
       List<Shift> findAllActivePastEndTime(
                     @Param("date") LocalDate date,
                     @Param("time") LocalTime time);

       @Query("SELECT s " +
                     "FROM Shift s " +
                     "JOIN s.vehicle v  " +
                     "WHERE s.status = 'OPENED' AND v.vehicleType.id = :vehicleTypeId")
       List<Shift> findShiftByVehicleTypeId(@Param("vehicleTypeId") UUID vehicleTypeId);
}
