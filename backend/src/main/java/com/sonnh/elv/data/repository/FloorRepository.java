package com.sonnh.elv.data.repository;

import com.sonnh.elv.data.domain.Floor;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FloorRepository extends JpaRepository<Floor, UUID> {
    List<Floor> findByTowerId(UUID towerId);
}
