package com.apihub.user.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserEmailLoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

}
