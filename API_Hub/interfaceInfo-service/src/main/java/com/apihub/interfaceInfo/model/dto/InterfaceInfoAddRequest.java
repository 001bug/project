package com.apihub.interfaceInfo.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 接口创建请求
 *
 */
@Data
public class InterfaceInfoAddRequest implements Serializable {

    /**
     * 名称
     */
    @ApiModelProperty(value = "接口名", required = true)
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty(value = "接口描述", required = false)
    private String description;

    /**
     * 接口地址
     */
    @ApiModelProperty(value = "接口url", required = true)
    private String url;

    /**
     * 请求参数
     */
    @ApiModelProperty(value = "请求参数", required = false)
    private String requestParams;

    /**
     * 请求头
     */
    @ApiModelProperty(value = "请求头", required = false)
    private String requestHeader;

    /**
     * 响应头
     */
    @ApiModelProperty(value = "响应头", required = false)
    private String responseHeader;

    /**
     * 请求类型
     */
    @ApiModelProperty(value = "请求方法", required = false)
    private String method;


    /**
     * 接口图片
     */
    @ApiModelProperty(value = "接口图片", required = false)
    private String image;
    /**
     * 价格（分）
     */
    @ApiModelProperty(value = "接口价格", required = false)
    private Integer price;
    /**
     * 类目名称
     */
    @ApiModelProperty(value = "接口所属类目", required = false)
    private String category;

}