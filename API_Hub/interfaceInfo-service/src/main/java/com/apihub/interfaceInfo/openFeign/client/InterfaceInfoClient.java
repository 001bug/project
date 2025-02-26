package com.apihub.interfaceInfo.openFeign.client;

import com.apihub.interfaceInfo.model.vo.UserVO;
import com.apihub.interfaceInfo.openFeign.config.DefaultFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "user-service",
        configuration = DefaultFeignConfig.class)
public interface InterfaceInfoClient {

    @GetMapping("/api/userRPC/get/login")
    UserVO getCurrentUser();

    @GetMapping("/api/userRPC/checkAdmin")
    Boolean checkAdmin();
}
