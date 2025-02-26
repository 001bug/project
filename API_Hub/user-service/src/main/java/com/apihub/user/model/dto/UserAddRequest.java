package com.apihub.user.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户创建请求
 *

 */
@Data
public class UserAddRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 账号
     */
    @ApiModelProperty(value = "用户名", required = true)
    private String userAccount;
    /**
    * 密码
    * */
    @ApiModelProperty(value = "密码", required = false)
    private String userPassword = "123456aa";
}