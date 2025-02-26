package com.apihub.pay.controller;


import com.apihub.common.common.BaseResponse;
import com.apihub.common.common.ErrorCode;
import com.apihub.common.common.ResultUtils;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.UserHolder;
import com.apihub.pay.model.dto.order.ApiOrderQueryRequest;
import com.apihub.pay.model.dto.order.DeductOrderDTO;
import com.apihub.pay.model.vo.ApiOrderVO;
import com.apihub.pay.openFeign.client.UserServiceClient;
import com.apihub.pay.service.ApiOrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "订单接口")
@RestController
@Slf4j
@RequestMapping("/order")
@RequiredArgsConstructor
public class ApiOrderController {

    private final UserServiceClient userServiceClient;
    @Resource
    private ApiOrderService apiOrderService;

    @ApiOperation("支付订单")
    @PostMapping("/deduct")
    public Boolean deductOrder(@RequestBody DeductOrderDTO deductOrderDTO){
        return apiOrderService.deductOrder(deductOrderDTO,UserHolder.getUser());
    }

    @ApiOperation("分页搜索api订单")
    @GetMapping("/list/page")
    public BaseResponse<Page<ApiOrderVO>> listApiOrderByPage(@RequestBody ApiOrderQueryRequest apiOrderQueryRequest) {
        //只允许查自己的api订单
        apiOrderQueryRequest.setUserId(UserHolder.getUser());
        return ResultUtils.success(apiOrderService.listPayOrderByPage(apiOrderQueryRequest));
    }
    @ApiOperation("管理员-分页搜索api订单")
    @GetMapping("/list/page/admin")
    public BaseResponse<Page<ApiOrderVO>> adminListApiOrderByPage(@RequestBody ApiOrderQueryRequest apiOrderQueryRequest) {
        if (!userServiceClient.checkAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(apiOrderService.listPayOrderByPage(apiOrderQueryRequest));
    }
}
