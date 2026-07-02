package com.sonnh.bookingcar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class })
@EnableJpaAuditing
@EnableScheduling
public class BookingCarApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookingCarApplication.class, args);

    }

}
