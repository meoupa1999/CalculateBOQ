package com.sonnh.elv.data.repository;

import com.sonnh.elv.data.domain.SpecialFloor;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SpecialFloorRepository extends JpaRepository<SpecialFloor, UUID> {
    List<SpecialFloor> findByTowerId(UUID towerId);
}
