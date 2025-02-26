package com.apihub.api.openFeign.client;

import com.apihub.api.model.domain.InterfaceInfo;
import com.apihub.api.openFeign.config.DefaultFeignConfig;
import com.apihub.api.openFeign.fallback.InterfaceInfoClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "interfaceInfo-service",
                configuration = DefaultFeignConfig.class,
                fallbackFactory = InterfaceInfoClientFallback.class)
public interface InterfaceInfoServiceClient {

    //获取接口信息
    @GetMapping("/api/interfaceInfoRPC/get")
    InterfaceInfo queryItemById(@RequestParam("id") long id);
}
