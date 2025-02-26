package com.apihub.voucher.model.dto.voucherinfo;

import lombok.Data;

import java.io.Serializable;

import static com.apihub.voucher.utils.VoucherInfoConstant.TYPE_BALANCE;

@Data
public class VoucherInfoAddRequest implements Serializable {
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
     * 支付金额，单位是分。例如200代表2元
     */
    private Long payValue;
    /**
     * 抵扣金额，单位是分。例如200代表2元
     */
    private Long actualValue;
    /**
     * 0,余额券；1,时段卡
     */
    private Integer type = TYPE_BALANCE;
    /**
     * 提供者id
     */
    private Long issuerId;

    /**
     * 时段卡生效时间
     */
    private String validTime = null;
}
