package com.apihub.pay.controller;


import com.apihub.common.common.BaseResponse;
import com.apihub.common.common.ErrorCode;
import com.apihub.common.common.ResultUtils;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.UserHolder;
import com.apihub.pay.model.dto.pay.ChargePayDTO;
import com.apihub.pay.model.dto.pay.PayOrderQueryRequest;
import com.apihub.pay.model.vo.PayOrderVO;
import com.apihub.pay.openFeign.client.UserServiceClient;
import com.apihub.pay.service.PayOrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "支付单接口")
@RestController
@Slf4j
@RequestMapping("/payOrder")
@RequiredArgsConstructor
public class PayOrderController {

    private final UserServiceClient userServiceClient;
    @Resource
    private PayOrderService payOrderService;

    @ApiOperation("充值支付单")
    @PostMapping("/charge")
    public BaseResponse<Boolean> chargePayOrder(@RequestBody ChargePayDTO chargePayDTO) {
        if (chargePayDTO.getAmount() == null || chargePayDTO.getAmount() < 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "充值数额不合法");
        }
        payOrderService.chargePayOrder(chargePayDTO);

        return ResultUtils.success(true);
    }


    //不支持手动创建支付单

    @ApiOperation("分页搜索支付单")
    @GetMapping("/list/page")
    public BaseResponse<Page<PayOrderVO>> listPayOrderByPage(@RequestBody PayOrderQueryRequest payOrderQueryRequest) {
        //只允许查询自己的订单
        payOrderQueryRequest.setBizUserId(UserHolder.getUser());
        return ResultUtils.success(payOrderService.listPayOrderByPage(payOrderQueryRequest));
    }

    @ApiOperation("管理员-分页搜索支付单")
    @GetMapping("/list/page/admin")
    public BaseResponse<Page<PayOrderVO>> adminListPayOrderByPage(@RequestBody PayOrderQueryRequest payOrderQueryRequest) {
        if (!userServiceClient.checkAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(payOrderService.listPayOrderByPage(payOrderQueryRequest));
    }
}
