package com.apihub.user.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description = "登录表单实体")
public class LoginFormDTO {
    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "账号不能为空")
    private String userAccount;

    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message="密码不能为空")
    private String userPassword;
}
