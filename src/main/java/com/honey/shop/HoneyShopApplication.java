package com.honey.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HoneyShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(HoneyShopApplication.class, args);
    }
}
