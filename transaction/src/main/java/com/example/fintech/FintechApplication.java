package com.example.fintech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.validation.annotation.Validated;

@SpringBootApplication
@EnableCaching
@Validated
public class FintechApplication {
    public static void main(String[] args) {
        SpringApplication.run(FintechApplication.class, args);
    }
}
