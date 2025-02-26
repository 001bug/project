package com.apihub.user.model.vo;

import lombok.Data;

@Data
public class UserBalanceVO {
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
}
