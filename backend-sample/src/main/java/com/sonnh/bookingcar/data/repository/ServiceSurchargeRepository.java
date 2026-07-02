package com.sonnh.bookingcar.data.repository;

import com.sonnh.bookingcar.data.domain.ServiceSurcharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ServiceSurchargeRepository extends JpaRepository<ServiceSurcharge, UUID> {
}
