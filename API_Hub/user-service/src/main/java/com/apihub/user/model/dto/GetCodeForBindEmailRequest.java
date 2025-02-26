package com.apihub.user.model.dto;


import lombok.Data;

import java.io.Serializable;


/*
 * 用户绑定邮箱请求体
 * */
@Data
public class GetCodeForBindEmailRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * userId
     */
    private Long userId;

    private String email;
}
