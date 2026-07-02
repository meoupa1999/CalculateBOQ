package com.sonnh.elv.data.repository;

import com.sonnh.elv.data.domain.Config;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends JpaRepository<Config, UUID> {
}
