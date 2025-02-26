package com.apihub.interfaceInfo.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口信息
 */
@Data
public class InterfaceInfoVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 请求参数
     */
    private String requestParams;
    /**
     * 请求头
     */
    private String requestHeader;
    /**
     * 响应头
     */
    private String responseHeader;
    /**
     * 接口状态（0-关闭，1-开启）
     */
    private Integer status;
    /**
     * 请求类型
     */
    private String method;
    /**
     * 创建人
     */
    private Long userId;
    /**
     * 接口图片
     */
    private String image;
    /**
     * 价格（分）
     */
    private Integer price;
    /**
     * 类目名称
     */
    private String category;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否删除(0-未删, 1-已删)
     */
    private Integer isDelete;
}