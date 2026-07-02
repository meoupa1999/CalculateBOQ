package com.sonnh.bookingcar.data.repository;

import com.sonnh.bookingcar.data.domain.SpecialLocationTour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpecialLocationTourRepository extends JpaRepository<SpecialLocationTour, UUID> {
    java.util.List<SpecialLocationTour> findByTourIdOrderByPriorityAsc(UUID tourId);
}
