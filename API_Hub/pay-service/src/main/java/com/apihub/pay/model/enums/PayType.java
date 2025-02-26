package com.apihub.pay.model.enums;

import lombok.Getter;

@Getter
public enum PayType {
    JSAPI(1, "网页支付JS"),
    MINI_APP(2, "小程序支付"),
    APP(3, "APP支付"),
    NATIVE(4, "扫码支付"),

    BALANCE_API_PAY(5, "api订单余额支付"),
    BALANCE_CHARGE(6, "余额充值"),

    BALANCE_VOUCHER_PAY(7, "voucher订单余额支付");
    private final int value;
    private final String desc;

    PayType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public boolean equalsValue(Integer value) {
        if (value == null) {
            return false;
        }
        return getValue() == value;
    }
}
