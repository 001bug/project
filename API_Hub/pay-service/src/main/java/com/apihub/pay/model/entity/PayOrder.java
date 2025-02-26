package com.apihub.pay.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 支付订单
 * @TableName pay_order
 */
@TableName(value ="pay_order")
@Data
public class PayOrder implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
     * 拓展字段
     */
    private String expandJson;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 第三方返回业务码
     */
    private String resultCode;
    /**
     * 第三方返回提示信息
     */
    private String resultMsg;
    /**
     * 支付成功时间
     */
    private Date paySuccessTime;
    /**
     * 支付超时时间
     */
    private Date payOverTime;
    /**
     * 支付二维码链接
     */
    private String qrCodeUrl;
    /**
     * 创建人
     */
    private Long creater;
    /**
     * 更新人
     */
    private Long updater;
    /**
     * 逻辑删除
     */
    private Boolean isDelete;
}