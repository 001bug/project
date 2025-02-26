package com.apihub.voucher;


import com.apihub.voucher.openFeign.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.apihub.voucher.mapper")
@EnableFeignClients(defaultConfiguration = DefaultFeignConfig.class)
@SpringBootApplication
public class VoucherApplication {
    public static void main(String[] args) {
        SpringApplication.run(VoucherApplication.class, args);
    }
}
