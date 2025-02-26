package com.apihub.voucher.service.impl;

import com.apihub.common.common.ErrorCode;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.UserHolder;
import com.apihub.voucher.mapper.VoucherOrderMapper;
import com.apihub.voucher.model.entity.VoucherInfo;
import com.apihub.voucher.model.entity.VoucherOrder;
import com.apihub.voucher.model.vo.VoucherOrderVO;
import com.apihub.voucher.service.VoucherInfoService;
import com.apihub.voucher.service.VoucherOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

import static com.apihub.voucher.utils.VoucherOrderConstant.ORDER_STATUS_UNPAID;

/**
 * @author IKUN
 * @description 针对表【voucher_order】的数据库操作Service实现
 * @createDate 2023-11-27 11:08:55
 */
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder>
        implements VoucherOrderService {

    @Resource
    private VoucherInfoService voucherInfoService;

    @Override
    public VoucherOrderVO createVoucherOrder(Long voucherId) {
        if (voucherId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        VoucherInfo voucherInfo = voucherInfoService.query().eq("id", voucherId).one();

        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setUserId(UserHolder.getUser());
        voucherOrder.setVoucherId(voucherId);
        voucherOrder.setStatus(ORDER_STATUS_UNPAID);

        voucherOrder.setVoucherType(voucherInfo.getType());

        voucherOrder.setCreateTime(new Date());

        boolean b = this.save(voucherOrder);
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }

        VoucherOrderVO voucherOrderVO = new VoucherOrderVO();
        BeanUtils.copyProperties(voucherOrder, voucherOrderVO);

        return voucherOrderVO;
    }
}




