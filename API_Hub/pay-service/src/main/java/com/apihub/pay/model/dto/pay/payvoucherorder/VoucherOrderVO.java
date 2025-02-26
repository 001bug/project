package com.apihub.pay.model.dto.pay.payvoucherorder;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @TableName voucher_order
 */
@Data
public class VoucherOrderVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 下单的用户id
     */
    private Long userId;
    /**
     * 购买的代金券id
     */
    private Long voucherId;
    /**
     * 优惠券类型
     */
    private Integer voucherType;
    /**
     * 支付方式 1：余额支付；2：支付宝；3：微信
     */
    private Integer payType;
    /**
     * 订单状态，1：未支付；2：已支付；
     * 3：已核销；4：已取消；5：退款中；6：已退款
     */
    private Integer status;
    /**
     * 支付时间
     */
    private Date payTime;
    /**
     * 核销时间
     */
    private Date useTime;
    /**
     * 退款时间/订单超时时间
     */
    private Date refundTime;
    /**
     * 优惠券有效期
     */
    private Date validDate;
    /**
     * 下单时间
     */
    private Date createTime;

}