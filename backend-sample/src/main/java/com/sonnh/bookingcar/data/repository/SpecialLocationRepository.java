package com.sonnh.bookingcar.data.repository;

import com.sonnh.bookingcar.data.domain.SpecialLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpecialLocationRepository extends JpaRepository<SpecialLocation, UUID>, JpaSpecificationExecutor<SpecialLocation> {
}
