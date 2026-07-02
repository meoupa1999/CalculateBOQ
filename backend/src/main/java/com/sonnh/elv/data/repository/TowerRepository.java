package com.sonnh.elv.data.repository;

import com.sonnh.elv.data.domain.Tower;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TowerRepository extends JpaRepository<Tower, UUID>, JpaSpecificationExecutor<Tower> {
}
