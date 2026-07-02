package com.sonnh.bookingcar.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;

@Component
@Order(1) // Run before UserDataSeeder and TourDataSeeder
@RequiredArgsConstructor
@Slf4j
public class DatabaseSchemaInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("Starting database schema synchronization...");
        try {
            // Fix service_requests table
            log.info("Checking service_requests table for missing columns...");
            
            // Add is_negotiated
            executeSafeSql("ALTER TABLE service_requests ADD COLUMN IF NOT EXISTS is_negotiated BOOLEAN DEFAULT FALSE");
            
            // Add negotiated_price
            executeSafeSql("ALTER TABLE service_requests ADD COLUMN IF NOT EXISTS negotiated_price NUMERIC(19, 2)");
            
            // Add driver_amount
            executeSafeSql("ALTER TABLE service_requests ADD COLUMN IF NOT EXISTS driver_amount NUMERIC(19, 2)");

            log.info("Database schema synchronization completed successfully.");
        } catch (Exception e) {
            log.error("Failed to synchronize database schema: {}", e.getMessage());
            // We don't throw exception to avoid stopping the server if it's already fixed
        }
    }

    private void executeSafeSql(String sql) {
        try {
            jdbcTemplate.execute(sql);
            log.debug("Successfully executed: {}", sql);
        } catch (Exception e) {
            // Some databases might not support IF NOT EXISTS or throw error if it fails
            log.warn("SQL Execution warning (might already exist): {} - {}", sql, e.getMessage());
        }
    }
}
