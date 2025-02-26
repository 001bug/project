package com.apihub.user.model.dto;

import com.apihub.common.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户查询请求
 *

 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "获取用户列表实体")
public class UserQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @ApiModelProperty(value = "要查询的id", required = false)
    private Long id;
    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "要查询的用户昵称", required = false)
    private String userName;
    /**
     * 账号
     */
    @ApiModelProperty(value = "要查询的用户账号", required = false)
    private String userAccount;
    /**
     * 用户头像
     */
    @ApiModelProperty(value = "要查询的用户头像", required = false)
    private String userAvatar;
    /**
     * 性别
     */
    @ApiModelProperty(value = "要查询的用户性别", required = false)
    private Integer gender;
    /**
     * 用户角色: user, admin
     */
    @ApiModelProperty(value = "要查询的用户权限", required = false)
    private String userRole;
    /**
     * 创建时间
     */
    @ApiModelProperty( required = false)
    private Date createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty(required = false)
    private Date updateTime;
}