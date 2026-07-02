package com.sonnh.bookingcar.data.repository;

import com.sonnh.bookingcar.data.domain.Tour;
import com.sonnh.bookingcar.data.domain.TourItinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TourItineraryRepository extends JpaRepository<TourItinerary, UUID>, JpaSpecificationExecutor<TourItinerary> {
    
    @Query("SELECT ti FROM TourItinerary ti WHERE ti.tour = :tour ORDER BY ti.orderIndex ASC")
    List<TourItinerary> findByTourOrderByOrderIndexAsc(@Param("tour") Tour tour);
}
