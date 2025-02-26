package com.apihub.voucher.controller;


import com.apihub.common.common.BaseResponse;
import com.apihub.common.common.ResultUtils;
import com.apihub.voucher.model.vo.VoucherOrderVO;
import com.apihub.voucher.service.VoucherOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/voucherOrder")
public class VoucherOrderController {

    @Resource
    private VoucherOrderService voucherOrderService;

    @PostMapping("/{voucherId}")
    public BaseResponse<VoucherOrderVO> createVoucherOrder(@PathVariable("voucherId") Long voucherId) {
        return ResultUtils.success(voucherOrderService.createVoucherOrder(voucherId));
    }

    @PostMapping("/seckill/{voucherId}")
    public BaseResponse<VoucherOrderVO> createSeckillVoucherOrder(@PathVariable("voucherId") Long voucherId) {

        //return voucherSeckillService.seckillVoucher(voucherId);
        return null;
    }
}
