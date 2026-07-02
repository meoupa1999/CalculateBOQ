package com.sonnh.bookingcar.data.repository;

import com.sonnh.bookingcar.data.domain.LicenseVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LicenseVerificationRepository extends JpaRepository<LicenseVerification, UUID> {
}
