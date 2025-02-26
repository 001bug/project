package com.apihub.interfaceInfo;


import com.apihub.interfaceInfo.openFeign.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients(defaultConfiguration = DefaultFeignConfig.class)
@MapperScan("com.apihub.interfaceInfo.mapper")
@SpringBootApplication
public class InterfaceInfoApplication {
    public static void main(String[] args) {
        SpringApplication.run(InterfaceInfoApplication.class, args);
    }
}
