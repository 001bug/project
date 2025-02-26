package com.apihub.pay.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName api_order
 */
@TableName(value ="api_order")
@Data
public class ApiOrder implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 订单id
     */
    @TableId(type = IdType.AUTO)
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
     * (所有调用均已付款)
     * 订单的状态，1、未付款 2、已付款,未发货 3、已发货,未确认 4、确认收货，交易成功 5、交易取消，订单关闭 6、交易结束，已评价
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 服务启用时间(时段卡)
     */
    private Date startTime;
    /**
     * 服务关闭时间(时段卡)
     */
    @TableField(value = "endTime",fill = FieldFill.INSERT)
    private Date endTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 拓展字段
     */
    private String otherInfo;
}