package com.apihub.voucher.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ApiTokenVO implements Serializable {

    String token;

    Long interfaceId;
    //生效时间
    Date beginTime;
    //失效时间
    Date endTime;

    Long userId;
}
