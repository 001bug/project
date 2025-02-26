package com.apihub.pay.controller;


import com.apihub.pay.model.dto.pay.payvoucherorder.VoucherBalancePayDTO;
import com.apihub.pay.service.PayOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "优惠券支付单接口")
@RestController
@Slf4j
@RequestMapping("/payVoucherOrderRPC")
public class PayVoucherOrderController {


    @Resource
    private PayOrderService payOrderService;

    @ApiOperation("余额支付")
    @PostMapping("/balance")
    public Boolean payVoucherOrder(@RequestBody VoucherBalancePayDTO deductPayDTO) {
        payOrderService.payVoucherOrder(deductPayDTO);
        return true;
    }
}
