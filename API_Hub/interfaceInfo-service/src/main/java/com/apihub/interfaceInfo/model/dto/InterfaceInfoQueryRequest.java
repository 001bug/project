package com.apihub.interfaceInfo.model.dto;


import com.apihub.common.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoQueryRequest extends PageRequest implements Serializable {

    /**
     * 主键
     */
    @ApiModelProperty(value = "接口id", required = false)
    private Long id;

    /**
     * 名称
     */
    @ApiModelProperty(value = "接口名称", required = false)
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty(value = "接口描述", required = false)
    private String description;

    /**
     * 接口地址
     */
    @ApiModelProperty(value = "接口url", required = false)
    private String url;

    /**
     * 请求头
     */
    @ApiModelProperty(value = "接口请求头", required = false)
    private String requestHeader;

    /**
     * 响应头
     */
    @ApiModelProperty(value = "响应头", required = false)
    private String responseHeader;

    /**
     * 接口状态（0-关闭，1-开启）
     */
    @ApiModelProperty(value = "接口状态（0-关闭，1-开启）", required = false)
    private Integer status;

    /**
     * 请求类型
     */
    @ApiModelProperty(value = "接口请求类型", required = false)
    private String method;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "接口创建人", required = false)
    private Long userId;

    /**
     * 上限价格（分）
     */
    @ApiModelProperty(value = "接口上限价格", required = false)
    private Integer topPrice = 100;

    /**
     * 下限价格（分）
     */
    @ApiModelProperty(value = "接口下限价格", required = false)
    private Integer lowPrice = 0;

    /**
     * 类目名称
     */
    @ApiModelProperty(value = "接口所属类目", required = false)
    private String category;

}