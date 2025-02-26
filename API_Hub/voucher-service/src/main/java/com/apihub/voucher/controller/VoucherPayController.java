package com.apihub.voucher.controller;


import com.apihub.common.common.BaseResponse;
import com.apihub.common.common.ErrorCode;
import com.apihub.common.common.ResultUtils;
import com.apihub.common.exception.BusinessException;
import com.apihub.voucher.model.dto.VoucherBalancePayDTO;
import com.apihub.voucher.model.entity.VoucherInfo;
import com.apihub.voucher.model.entity.VoucherOrder;
import com.apihub.voucher.model.vo.VoucherOrderVO;
import com.apihub.voucher.openFeign.client.PayServiceClient;
import com.apihub.voucher.service.VoucherInfoService;
import com.apihub.voucher.service.VoucherOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

import static com.apihub.voucher.utils.VoucherOrderConstant.ORDER_STATUS_PAID;
import static com.apihub.voucher.utils.VoucherOrderConstant.ORDER_STATUS_UNPAID;

@RestController
@Slf4j
@RequestMapping("/voucherPay")
@RequiredArgsConstructor
public class VoucherPayController {

    @Resource
    private VoucherOrderService voucherOrderService;

    private final PayServiceClient payServiceClient;
    @Resource
    private VoucherInfoService voucherInfoService;

    @PostMapping("/{voucherOrderId}")
    public BaseResponse<Boolean> VoucherPay(@PathVariable("voucherOrderId") Long voucherOrderId) {
        //检查是否为待支付状态
        VoucherOrder voucherOrder = voucherOrderService.query().eq("id", voucherOrderId).one();
        Integer payType = voucherOrder.getPayType();
        if (!Objects.equals(payType, ORDER_STATUS_UNPAID)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "订单未处于未支付状态！");
        }

        //检查是否扣款成功
        VoucherOrderVO voucherOrderVO = new VoucherOrderVO();
        BeanUtils.copyProperties(voucherOrder, voucherOrderVO);

        VoucherInfo voucherInfo = voucherInfoService.query().eq("id", voucherOrder.getVoucherId()).one();
        Long payValue = voucherInfo.getPayValue();

        VoucherBalancePayDTO voucherBalancePayDTO = new VoucherBalancePayDTO();
        voucherBalancePayDTO.setVoucherOrderVO(voucherOrderVO);
        voucherBalancePayDTO.setAmount(Math.toIntExact(payValue));

        if (!payServiceClient.payVoucherOrder(voucherBalancePayDTO)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR);
        }

        //修改订单状态
        voucherOrder.setStatus(ORDER_STATUS_PAID);
        boolean b = voucherOrderService.updateById(voucherOrder);
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(true);
    }
}
