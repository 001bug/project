package com.apihub.voucher.utils;

/**
 * 优惠券信息常量
 */
public interface VoucherInfoConstant {

    //0,余额券；1,时段卡
    Integer TYPE_BALANCE = 0;
    Integer TYPE_INTERFACE = 1;
    Integer TYPE_SECKILL_BALANCE = 2;
    Integer TYPE_SECKILL_INTERFACE = 3;

    //1,上架；2,下架；3,过期
    Integer STATUS_UP = 1;
    Integer STATUS_DOWN = 2;
    Integer STATUS_EXPIRED = 3;

}
