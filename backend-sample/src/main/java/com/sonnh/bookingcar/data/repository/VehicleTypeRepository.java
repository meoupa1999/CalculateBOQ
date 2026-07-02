package com.sonnh.bookingcar.data.repository;

import com.sonnh.bookingcar.data.domain.VehicleType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleTypeRepository extends JpaRepository<VehicleType, UUID>, JpaSpecificationExecutor<VehicleType> {
    
    @Query("SELECT vt FROM VehicleType vt WHERE vt.name = :name")
    Optional<VehicleType> findByName(@Param("name") String name);
}
