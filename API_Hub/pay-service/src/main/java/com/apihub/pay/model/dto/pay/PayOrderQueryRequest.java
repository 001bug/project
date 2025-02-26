package com.apihub.pay.model.dto.pay;

import com.apihub.common.common.PageRequest;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "获取支付单列表实体")
public class PayOrderQueryRequest extends PageRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 4L;

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
     * 支付金额（上限）
     */
    private Integer maxAmount;
    /**
     * 支付金额（下限）
     */
    private Integer minAmount;

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
}
