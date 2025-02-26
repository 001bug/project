package com.apihub.voucher.model.dto.seckillinfo;

import com.apihub.common.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;


import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 秒杀优惠券表
 *
 * @TableName voucher_seckill
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SeckillVoucherInfoListByPageRequest extends PageRequest implements Serializable {
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