package com.sonnh.bookingcar.data.repository;

import com.sonnh.bookingcar.data.domain.TourHighlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TourHighlightRepository extends JpaRepository<TourHighlight, UUID>, JpaSpecificationExecutor<TourHighlight> {
}
