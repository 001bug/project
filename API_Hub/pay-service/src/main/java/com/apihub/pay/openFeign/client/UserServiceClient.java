package com.apihub.pay.openFeign.client;

import com.apihub.pay.openFeign.config.DefaultFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service",
        configuration = DefaultFeignConfig.class)
public interface UserServiceClient {

    @PutMapping("/api/user/balance/charge")
    Boolean chargeBalance(@RequestParam("amount") Integer amount);

    @GetMapping("/api/userRPC/checkAdmin")
    Boolean checkAdmin();

    @PutMapping("/api/user/balance/deduct")
    Boolean deductBalance(@RequestParam("amount") Integer amount);
}
