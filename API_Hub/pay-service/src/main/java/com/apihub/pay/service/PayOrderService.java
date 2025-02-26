package com.apihub.pay.service;

import com.apihub.pay.model.dto.pay.ChargePayDTO;
import com.apihub.pay.model.dto.pay.PayOrderQueryRequest;
import com.apihub.pay.model.dto.pay.payvoucherorder.VoucherBalancePayDTO;
import com.apihub.pay.model.entity.PayOrder;
import com.apihub.pay.model.vo.PayOrderVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author IKUN
 * @description 针对表【pay_order(支付订单)】的数据库操作Service
 * @createDate 2023-11-02 11:58:00
 */
public interface PayOrderService extends IService<PayOrder> {

    void chargePayOrder(ChargePayDTO chargePayDTO);

    Page<PayOrderVO> listPayOrderByPage(PayOrderQueryRequest payOrderQueryRequest);

    void payVoucherOrder(VoucherBalancePayDTO deductPayDTO);
}
