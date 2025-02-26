package com.apihub.user.model.dto.aouth;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class BindGiteeRequest implements Serializable {
    @ApiModelProperty(value = "gitee邮箱", required = true)
    private String email;
    @ApiModelProperty(value = "gitee密码", required = true)
    private String password;

    //1 将gitee的信息填充到空缺的个人信息中
    //2 不填充信息，单纯绑定
    @ApiModelProperty(value = "绑定模式", required = true)
    private Long mode;
}
