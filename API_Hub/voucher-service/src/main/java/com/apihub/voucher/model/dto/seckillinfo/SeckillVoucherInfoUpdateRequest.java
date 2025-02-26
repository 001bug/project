package com.apihub.voucher.model.dto.seckillinfo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 秒杀优惠券表
 *
 * @TableName voucher_seckill
 */
@Data
public class SeckillVoucherInfoUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 关联的优惠券的id
     */
    private Long voucherId;
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
     * 拓展字段
     */
    private String otherInfo;

}