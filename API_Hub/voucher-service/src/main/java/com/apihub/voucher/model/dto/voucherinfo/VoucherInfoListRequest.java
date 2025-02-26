package com.apihub.voucher.model.dto.voucherinfo;

import lombok.Data;

import java.io.Serializable;

@Data
public class VoucherInfoListRequest implements Serializable {
    /**
     * 主键
     */
    private Long id;
    /**
     * 代金券标题
     */
    private String title;
    /**
     * 副标题
     */
    private String subTitle;
    /**
     * 使用规则
     */
    private String rules;
    /**
     * 优惠接口id
     */
    private Long interfaceId;

    /**
     * 支付上限金额，单位是分。例如200代表2元
     */
    private Long payValueTop;
    /**
     * 支付下限金额，单位是分。例如200代表2元
     */
    private Long payValueFloor;

    /**
     * 抵扣上限金额，单位是分。例如200代表2元
     */
    private Long actualValueTop;
    /**
     * 抵扣下限金额，单位是分。例如200代表2元
     */
    private Long actualValueFloor;


    /**
     * 0,余额券；1,时段卡
     */
    private Integer type;
    /**
     * 提供者id
     */
    private Long issuerId;
}
