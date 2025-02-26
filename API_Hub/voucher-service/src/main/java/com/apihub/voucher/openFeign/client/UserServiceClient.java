package com.apihub.voucher.openFeign.client;

import com.apihub.voucher.openFeign.config.DefaultFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "user-service",
        configuration = DefaultFeignConfig.class)
public interface UserServiceClient {


    @GetMapping("/api/userRPC/checkAdmin")
    Boolean checkAdmin();
}
