package com.apihub.user.controller;

import com.apihub.user.model.vo.UserBalanceVO;
import com.apihub.user.service.UserBalancePaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(tags = "用户余额RPC接口")
@RestController
@Slf4j
@RequestMapping("/user/balance")
public class userBalanceRPCController {
    //todo 优化：将用户扣款信息存储到redis中
    //目前直接使用MySQL来操作
    //如果使用redis，需要考虑MySQL与redis的同步；比如：每天同步mysql与redis中的用户余额信息

    @Resource
    private UserBalancePaymentService userBalancePaymentService;

    @ApiOperation("查询余额")
    @GetMapping("/get")
    public UserBalanceVO getBalance(HttpServletRequest request){
        return userBalancePaymentService.getBalance(request);
    }

    @ApiOperation("余额扣减")
    @PutMapping("/deduct")
    public Boolean deductBalance(@RequestParam("amount") Integer amount){
        return userBalancePaymentService.deductBalance(amount);
    }

    @ApiOperation("余额充值")
    @PutMapping("/charge")
    public Boolean chargeBalance(@RequestParam("amount") Integer amount){
        return userBalancePaymentService.chargeBalance(amount);
    }

}
