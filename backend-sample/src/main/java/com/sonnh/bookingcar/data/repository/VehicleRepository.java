package com.sonnh.bookingcar.data.repository;

import com.sonnh.bookingcar.data.domain.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID>, JpaSpecificationExecutor<Vehicle> {
    
    @Query("SELECT COUNT(v) > 0 FROM Vehicle v WHERE v.plateNumber = :plateNumber")
    boolean existsByPlateNumber(@Param("plateNumber") String plateNumber);

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.audit.isActive = true AND (:status IS NULL OR v.status = :status)")
    long countByStatus(@Param("status") com.sonnh.bookingcar.data.domain.enums.VehicleStatus status);

    @Query("SELECT DISTINCT v FROM Vehicle v LEFT JOIN FETCH v.documents d WHERE v.audit.isActive = true")
    List<Vehicle> findAllActiveVehiclesWithDocuments();
}
