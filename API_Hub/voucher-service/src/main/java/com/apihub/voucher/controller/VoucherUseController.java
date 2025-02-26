package com.apihub.voucher.controller;


import com.apihub.common.common.BaseResponse;
import com.apihub.common.common.ErrorCode;
import com.apihub.common.common.ResultUtils;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.UserHolder;
import com.apihub.voucher.model.dto.UseVoucherRequest;
import com.apihub.voucher.model.entity.VoucherInfo;
import com.apihub.voucher.model.entity.VoucherOrder;
import com.apihub.voucher.model.otherInfo.VoucherInfoOtherInfo;
import com.apihub.voucher.model.vo.ApiTokenVO;
import com.apihub.voucher.service.VoucherInfoService;
import com.apihub.voucher.service.VoucherOrderService;
import com.apihub.voucher.utils.JwtTool;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;

import static com.apihub.voucher.utils.VoucherInfoConstant.TYPE_INTERFACE;
import static com.apihub.voucher.utils.VoucherInfoConstant.TYPE_SECKILL_INTERFACE;
import static com.apihub.voucher.utils.VoucherOrderConstant.ORDER_STATUS_PAID;
import static com.apihub.voucher.utils.VoucherOrderConstant.ORDER_STATUS_VERIFIED;

@RestController
@Slf4j
@RequestMapping("/apiToken")
@RequiredArgsConstructor
public class VoucherUseController {
    private final JwtTool jwtTool;
    @Resource
    private VoucherOrderService voucherOrderService;
    @Resource
    private VoucherInfoService voucherInfoService;

    @PostMapping("/")
    public BaseResponse<ApiTokenVO> useVoucher(@RequestBody UseVoucherRequest useVoucherRequest,
                                               HttpServletRequest request) {
        Long voucherOrderId = useVoucherRequest.getVoucherOrderId();
        if (voucherOrderId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单id缺失");
        }
        //查询优惠券是否为时段卡
        VoucherOrder voucherOrder = voucherOrderService.query().eq("id", voucherOrderId).one();
        if (voucherOrder == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未查询到订单");
        }
        Integer voucherType = voucherOrder.getVoucherType();
        if (!Objects.equals(voucherType, TYPE_INTERFACE)
                && !Objects.equals(voucherType, TYPE_SECKILL_INTERFACE)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不是时段卡订单");
        }
        Date beginTime = useVoucherRequest.getBeginTime();
        if (beginTime == null || beginTime.before(new Date())) {
            beginTime = new Date();
        }

        //查询优惠券是否可用
        if (!Objects.equals(voucherOrder.getStatus(), ORDER_STATUS_PAID)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单未支付");
        }

        //获取voucherInfo,endTime
        Long voucherId = voucherOrder.getVoucherId();
        VoucherInfo voucherInfo = voucherInfoService.query().eq("id", voucherId).one();
        String otherInfo = voucherInfo.getOtherInfo();
        Gson gson = new Gson();
        VoucherInfoOtherInfo voucherInfoOtherInfo = gson.fromJson(otherInfo, VoucherInfoOtherInfo.class);
        long validTimeSecond = Long.parseLong(voucherInfoOtherInfo.getValidTime());
        Date endTime = new Date(beginTime.toInstant().plusSeconds(validTimeSecond).toEpochMilli());

        //签发token
        //token只包含userId
        String token = jwtTool.createToken(UserHolder.getUser(), endTime);

        //更改订单信息
        voucherOrder.setStatus(ORDER_STATUS_VERIFIED);
        voucherOrder.setUseTime(beginTime);
        boolean b = voucherOrderService.updateById(voucherOrder);
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }

        //将interfaceId和accessKey包装进去
        ApiTokenVO apiTokenVO = new ApiTokenVO();
        apiTokenVO.setInterfaceId(voucherInfo.getInterfaceId());
        apiTokenVO.setBeginTime(beginTime);
        apiTokenVO.setEndTime(endTime);
        apiTokenVO.setToken(token);
        //返回token,interfaceId,beginTime,endTime
        return ResultUtils.success(apiTokenVO);
    }

}
