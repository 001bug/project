package com.apihub.pay.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class ApiOrderVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 订单id
     */
    private Long id;
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
     * 用户id
     */
    private Long userId;
    /**
     * 使用次数
     */
    private Long num;
    /**
     * 用户的id地址
     */
    private String userAddress;
    /**
     * 订单的状态，1、未付款 2、已付款,未发货 3、已发货,未确认 4、确认收货，交易成功 5、交易取消，订单关闭 6、交易结束，已评价
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 服务关闭时间
     */
    private Date endTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}