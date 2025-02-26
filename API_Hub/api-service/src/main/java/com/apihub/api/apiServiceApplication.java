package com.apihub.api;


import com.apihub.api.openFeign.config.DefaultFeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients(defaultConfiguration = DefaultFeignConfig.class)
@SpringBootApplication
public class apiServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(apiServiceApplication.class, args);
    }
}
