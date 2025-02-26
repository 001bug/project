package com.apihub.voucher.model.dto;


import lombok.Data;

import java.util.Date;

@Data
public class UseVoucherRequest {
    //订单id
    private Long voucherOrderId;
    //选定时间
    private Date beginTime;
}
