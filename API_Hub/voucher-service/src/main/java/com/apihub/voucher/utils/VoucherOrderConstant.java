package com.apihub.voucher.utils;

/**
 * 优惠券信息常量
 */
public interface VoucherOrderConstant {

    //1,未支付；2,已支付；3,已核销；4,已取消；5,退款中；6,退款成功
    Integer ORDER_STATUS_UNPAID = 1;
    Integer ORDER_STATUS_PAID = 2;
    Integer ORDER_STATUS_VERIFIED = 3;
    Integer ORDER_STATUS_CANCELED = 4;
    Integer ORDER_STATUS_REFUNDING = 5;
    Integer ORDER_STATUS_REFUNDED = 6;

}