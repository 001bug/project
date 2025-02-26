package com.apihub.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户余额表
 * @TableName user_balance_payment
 */
@TableName(value ="user_balance_payment")
@Data
public class UserBalancePayment implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 用户余额
     */
    private Long balance;
    /**
     * 冻结资金-----用于退款等
     */
    private Long frozenAmount;
    /**
     * 用户积分
     */
    private Long score;
    /**
     * 消费金额
     */
    private Long expenseAmount;
    /**
     * 消费积分
     */
    private Long expenseScore;
    /**
     * 消费次数
     */
    private Long expenseCount;
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
     * 是否删除
     */
    private Integer isDelete;
}