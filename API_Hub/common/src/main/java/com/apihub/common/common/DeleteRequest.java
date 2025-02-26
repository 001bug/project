package com.apihub.common.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 *

 */
@Data
public class DeleteRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @ApiModelProperty(value = "删除id", required = true)
    private Long id;
}

// https://www.code-nav.cn/