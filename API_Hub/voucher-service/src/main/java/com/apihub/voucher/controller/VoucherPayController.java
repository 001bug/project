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

    /**
     * 处理凭证支付请求的POST方法。
     * 该方法接收一个凭证订单ID，检查订单状态，进行扣款操作，并更新订单状态。
     *
     * @param voucherOrderId 凭证订单ID
     * @return 返回支付结果
     */
    @PostMapping("/{voucherOrderId}")
    public BaseResponse<Boolean> VoucherPay(@PathVariable("voucherOrderId") Long voucherOrderId) {
        //检查是否为待支付状态
        VoucherOrder voucherOrder = voucherOrderService.query().eq("id", voucherOrderId).one();
        Integer payType = voucherOrder.getPayType();
        // 如果订单状态不是未支付，抛出业务异常
        if (!Objects.equals(payType, ORDER_STATUS_UNPAID)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "订单未处于未支付状态！");
        }

        //检查是否扣款成功
        VoucherOrderVO voucherOrderVO = new VoucherOrderVO();
        // 将VoucherOrder对象的属性复制到VoucherOrderVO对象
        BeanUtils.copyProperties(voucherOrder, voucherOrderVO);

        // 查询凭证信息
        VoucherInfo voucherInfo = voucherInfoService.query().eq("id", voucherOrder.getVoucherId()).one();
        Long payValue = voucherInfo.getPayValue();

        // 创建支付请求DTO对象
        VoucherBalancePayDTO voucherBalancePayDTO = new VoucherBalancePayDTO();
        voucherBalancePayDTO.setVoucherOrderVO(voucherOrderVO);
        voucherBalancePayDTO.setAmount(Math.toIntExact(payValue));

        // 调用支付服务进行扣款
        if (!payServiceClient.payVoucherOrder(voucherBalancePayDTO)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR);
        }

        //修改订单状态
        voucherOrder.setStatus(ORDER_STATUS_PAID);
        // 更新订单状态
        boolean b = voucherOrderService.updateById(voucherOrder);
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        // 返回支付成功的结果
        return ResultUtils.success(true);
    }

}
