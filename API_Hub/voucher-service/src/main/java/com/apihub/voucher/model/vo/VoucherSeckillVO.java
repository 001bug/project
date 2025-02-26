package com.apihub.voucher.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class VoucherSeckillVO implements Serializable {

    private Long id;
    /**
     * 关联的优惠券的id
     */
    private Long voucherId;
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
     * 创建时间
     */
    private Date createTime;
}
