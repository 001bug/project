package com.apihub.user.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "登录表单实体")
public class LoginFormDTO {
    @ApiModelProperty(value = "用户名", required = true)
    private String userAccount;

    @ApiModelProperty(value = "密码", required = true)
    private String userPassword;
}
