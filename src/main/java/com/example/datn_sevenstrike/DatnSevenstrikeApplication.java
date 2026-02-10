package com.example.datn_sevenstrike;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class DatnSevenstrikeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatnSevenstrikeApplication.class, args);
    }

}
