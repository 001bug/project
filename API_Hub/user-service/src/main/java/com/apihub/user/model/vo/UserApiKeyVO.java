package com.apihub.user.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户创建请求
 *

 */
@Data
public class UserApiKeyVO implements Serializable {
    private static final long serialVersionUID = 1L;


    private String accessKey;

    private String sign;
}