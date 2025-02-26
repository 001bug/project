package com.apihub.pay.model.dto;


import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "服务调用订单实体")
public class DeductOrderMqDTO {
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 总金额，单位为分
     */
    private Integer totalFee;

    /**
     * 支付类型，1、支付宝，2、微信，3、扣减余额
     */
    private Integer paymentType;

    /**
     * 接口id
     */
    private Long interfaceId;

    /**
     * 使用次数
     */
    private Long num;

    /**
     * 用户的id地址
     */
    private String userAddress;
}
