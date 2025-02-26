package com.apihub.api.controller;


import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.apihub.api.model.domain.InterfaceInfo;
import com.apihub.api.model.dto.DeductOrderMqDTO;
import com.apihub.api.openFeign.client.InterfaceInfoServiceClient;
import com.apihub.api.openFeign.client.UserServiceClient;
import com.apihub.api.utils.GetIpAddressUtils;
import com.apihub.common.common.BaseResponse;
import com.apihub.common.common.ErrorCode;
import com.apihub.common.common.ResultUtils;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.UserHolder;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.apihub.common.utils.RedisConstants.*;

@RestController
@RequestMapping("/apiService")
@Slf4j
@RequiredArgsConstructor
public class ApiController {
    private final InterfaceInfoServiceClient interfaceInfoServiceClient;
    private final UserServiceClient userServiceClient;

    private final RabbitTemplate rabbitTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //todo 需要开启全局事务
    @ApiOperation("get请求接口")
    @GetMapping("/get")
    public BaseResponse<Object> getInterfaceInfoById(@RequestParam("InterfaceId") long interfaceId,
                                                     @RequestParam("body") String body,
                                                     HttpServletRequest request) {
        //查询接口url及其他信息通过服务远程调用
         InterfaceInfo interfaceInfo = interfaceInfoServiceClient.queryItemById(interfaceId);

        checkInfoAndDeduct(interfaceInfo, "get");

        //向url发请求
        String requestBody = URLUtil.decode(body, CharsetUtil.CHARSET_UTF_8);
        String param = StringUtils.isEmpty(requestBody) ? "" : ("?" + requestBody);
        HttpResponse httpResponse = HttpRequest.post(interfaceInfo.getUrl() + param)
                .execute();

        //请求成功，使用MQ队列 ,?发送扣费订单?
        sendMQ(interfaceInfo, request);

        return ResultUtils.success(httpResponse.body());
    }


    @ApiOperation("post请求接口")
    @PostMapping("/post")
    public BaseResponse<Object> postInterfaceInfoById(@RequestParam("InterfaceId") long interfaceId,
                                                      @RequestParam("body") String body,
                                                      HttpServletRequest request) {
        //查询接口url及其他信息
        InterfaceInfo interfaceInfo = interfaceInfoServiceClient.queryItemById(interfaceId);
        checkInfoAndDeduct(interfaceInfo, "post");
        //向url发请求
        String requestBody = URLUtil.decode(body, CharsetUtil.CHARSET_UTF_8);
        HttpResponse httpResponse = HttpRequest.post(interfaceInfo.getUrl() + "?" + requestBody)
                .execute();

        //请求成功，使用MQ队列
        sendMQ(interfaceInfo, request);

        return ResultUtils.success(httpResponse.body());
    }

    private void checkInfoAndDeduct(InterfaceInfo interfaceInfo, String method) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未查询到有效接口");
        }
        //限流操作，返回为0则是限流
        if (interfaceInfo.getId() == 0) {
            //System.out.println("id===0---------------------------------------------");
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "服务器繁忙，请稍后重试");
        }
        //查看是否为第三方接口
        if (!Objects.equals(interfaceInfo.getCategory(), "0")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求接口不是第三方接口");
        }
        // 利用字符串查找子串的方式判断方法是否存在
        if (!interfaceInfo.getMethod().contains(method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求方法错误");
        }
        Integer interfaceInfoPrice = interfaceInfo.getPrice();
        //检查用户余额，并进行扣款
        if (!userServiceClient.deductBalance(interfaceInfoPrice)) {
            //如果不够次数，直接返回结果
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "用户余额不足");
        }
        // 扣款成功,接口调用次数 + 1
        stringRedisTemplate.opsForValue().increment(API_INVOKE_KEY + interfaceInfo.getId(), 1L);
    }

    private void sendMQ(InterfaceInfo interfaceInfo, HttpServletRequest request) {
        //请求成功，使用MQ队列
        //仅记录订单
        //使用try catch，因为错误记录可以人工更改，但是钱必须先扣
        try {
            DeductOrderMqDTO deductOrderMqDTO = new DeductOrderMqDTO();
            deductOrderMqDTO.setInterfaceId(interfaceInfo.getId());
            deductOrderMqDTO.setNum(1L);
            deductOrderMqDTO.setPaymentType(3);
            deductOrderMqDTO.setTotalFee(interfaceInfo.getPrice());
            //返回发送请求的客户端或最后一个代理的Internet协议（IP）地址
            deductOrderMqDTO.setUserAddress(GetIpAddressUtils.GetIpAddress(request));
            deductOrderMqDTO.setUserId(UserHolder.getUser());
            log.info("发送MQ队列请求");
            rabbitTemplate.convertAndSend("payService.topic", "order.success", deductOrderMqDTO);
        } catch (Exception e) {
            log.error("MQ队列出错，订单发送失败：", e);
        }
    }
}
