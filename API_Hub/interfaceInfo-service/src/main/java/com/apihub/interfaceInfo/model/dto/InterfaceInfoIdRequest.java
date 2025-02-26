package com.apihub.interfaceInfo.model.dto;

// https://space.bilibili.com/12890453/

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 */
@Data
public class InterfaceInfoIdRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @ApiModelProperty(value = "修改接口id", required = true)
    private Long id;
}