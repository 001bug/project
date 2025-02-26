package com.apihub.voucher.openFeign.client;


import com.apihub.voucher.model.dto.VoucherBalancePayDTO;
import com.apihub.voucher.openFeign.config.DefaultFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "pay-service",
        configuration = DefaultFeignConfig.class)
public interface PayServiceClient {

    @PostMapping("/api/payVoucherOrderRPC/balance")
    Boolean payVoucherOrder(@RequestBody VoucherBalancePayDTO deductPayDTO);
}
