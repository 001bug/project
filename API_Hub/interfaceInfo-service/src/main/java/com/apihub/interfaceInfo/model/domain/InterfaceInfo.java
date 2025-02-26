package com.apihub.interfaceInfo.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口信息
 * @TableName interface_info
 */
@TableName(value ="interface_info")
@Data
public class InterfaceInfo implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
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
     * 接口地址
     */
    private String url;
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
     * 广告推广力度，0-100
     */
    private Integer isAD;
    /**
     * 类目名称
     */
    private String category;
    /**
     * 拓展信息
     */
    private String otherInfo;
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