package com.apihub.user.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class VerifyCodeForResetPasswordRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private String verifyCode;
    private Long userId;

    private String newPassword;
}
