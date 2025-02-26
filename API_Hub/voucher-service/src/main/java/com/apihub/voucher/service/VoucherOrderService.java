package com.apihub.voucher.service;

import com.apihub.voucher.model.entity.VoucherOrder;
import com.apihub.voucher.model.vo.VoucherOrderVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author IKUN
 * @description 针对表【voucher_order】的数据库操作Service
 * @createDate 2023-11-27 11:08:55
 */
public interface VoucherOrderService extends IService<VoucherOrder> {

    VoucherOrderVO createVoucherOrder(Long voucherId);
}
