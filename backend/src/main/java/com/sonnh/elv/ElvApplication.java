package com.sonnh.elv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ElvApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElvApplication.class, args);
    }

}
