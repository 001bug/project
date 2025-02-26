package com.apihub.pay.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 支付订单
 * @TableName pay_order
 */
@Data
public class PayOrderVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
    /**
     * 业务订单号
     */
    private Long bizOrderNo;
    /**
     * 支付单号
     */
    private Long payOrderNo;
    /**
     * 支付用户id
     */
    private Long bizUserId;
    /**
     * 支付渠道编码
     */
    private String payChannelCode;
    /**
     * 支付金额，单位分
     */
    private Integer amount;
    /**
     * 支付类型，1：h5,2:小程序，3：公众号，4：扫码，5：余额支付
     */
    private Integer payType;
    /**
     * 支付状态，0：待提交，1:待支付，2：支付超时或取消，3：支付成功
     */
    private Integer status;
    /**
     * 优惠券id
     */
    private Long couponId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 创建人
     */
    private Long creater;
}