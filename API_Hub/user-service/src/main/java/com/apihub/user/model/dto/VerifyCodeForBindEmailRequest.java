package com.apihub.user.model.dto;

import lombok.Data;

@Data
public class VerifyCodeForBindEmailRequest {

    /**
     * 验证码
     */
    private String code;

    /**
     * userId
     */
    private Long userId;

    /*
     * 邮箱
     * */
    private String email;
}
