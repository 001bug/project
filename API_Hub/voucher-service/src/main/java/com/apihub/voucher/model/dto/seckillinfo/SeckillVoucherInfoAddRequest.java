package com.apihub.voucher.model.dto.seckillinfo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import static com.apihub.voucher.utils.VoucherInfoConstant.TYPE_SECKILL_BALANCE;

@Data
public class SeckillVoucherInfoAddRequest implements Serializable {
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
     * 0,余额券；1,时段卡；2,秒杀余额券；3,秒杀时段卡
     */
    private Integer type = TYPE_SECKILL_BALANCE;
    /**
     * 提供者id
     */
    private Long issuerId;

    /**
     * 库存
     */
    private Integer stock;
    /**
     * 生效时间
     */
    private Date beginTime;
    /**
     * 失效时间
     */
    private Date endTime;
    /**
     * 备注
     */
    private String remark;

    /**
     * 时段卡生效起始时间
     */
    private String validTime;
}
