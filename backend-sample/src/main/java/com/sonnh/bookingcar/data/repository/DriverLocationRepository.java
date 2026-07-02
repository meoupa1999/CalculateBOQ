package com.sonnh.bookingcar.data.repository;

import com.sonnh.bookingcar.data.domain.DriverLocation;
import com.sonnh.bookingcar.data.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverLocationRepository extends JpaRepository<DriverLocation, UUID>, JpaSpecificationExecutor<DriverLocation> {
    Optional<DriverLocation> findByDriver(User driver);
    Optional<DriverLocation> findByDriverId(UUID driverId);
}
