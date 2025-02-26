package com.apihub.pay.model.enums;

public enum OrderStatus {
    NOT_GENERATED_NOT_PAID(1),
    NOT_GENERATED_PAID(2),
    GENERATED_NOT_PAID(3),
    GENERATED_PAID(4);

    private final int code;

    OrderStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
