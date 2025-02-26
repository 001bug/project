package com.apihub.voucher.service.impl;

import com.apihub.common.common.ErrorCode;
import com.apihub.common.exception.BusinessException;
import com.apihub.voucher.mapper.VoucherSeckillMapper;
import com.apihub.voucher.model.dto.seckillinfo.SeckillVoucherInfoListByPageRequest;
import com.apihub.voucher.model.dto.seckillinfo.SeckillVoucherInfoListRequest;
import com.apihub.voucher.model.dto.seckillinfo.SeckillVoucherInfoUpdateRequest;
import com.apihub.voucher.model.dto.voucherinfo.VoucherInfoDelRequest;
import com.apihub.voucher.model.entity.VoucherSeckill;
import com.apihub.voucher.model.vo.VoucherSeckillVO;
import com.apihub.voucher.openFeign.client.UserServiceClient;
import com.apihub.voucher.service.VoucherSeckillService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author IKUN
 * @description 针对表【voucher_seckill(秒杀优惠券表)】的数据库操作Service实现
 * @createDate 2023-11-22 20:48:34
 */
@Service
@RequiredArgsConstructor
public class VoucherSeckillServiceImpl extends ServiceImpl<VoucherSeckillMapper, VoucherSeckill>
        implements VoucherSeckillService {

    private final UserServiceClient userServiceClient;

    @Override
    public void delSeckillVoucherInfo(VoucherInfoDelRequest voucherInfoDelRequest) {
        //删除秒杀信息
        if (!userServiceClient.checkAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        Long id = voucherInfoDelRequest.getId();
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = this.removeById(id);
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }

        //删除基本信息中秒杀信息id（暂不删除）
    }

    @Override
    public void updateSeckillVoucherInfo(SeckillVoucherInfoUpdateRequest seckillVoucherInfoUpdateRequest) {
        //只有管理员才能添加
        if (!userServiceClient.checkAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        VoucherSeckill updateVoucherInfo = new VoucherSeckill();
        BeanUtils.copyProperties(seckillVoucherInfoUpdateRequest, updateVoucherInfo);
        boolean b = this.updateById(updateVoucherInfo);
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }

    @Override
    public VoucherSeckillVO getSeckillVoucherInfoById(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        VoucherSeckill voucherSeckill = this.query().eq("id", id).one();
        VoucherSeckillVO voucherSeckillVO = new VoucherSeckillVO();
        BeanUtils.copyProperties(voucherSeckill, voucherSeckillVO);
        return voucherSeckillVO;
    }

    @Override
    public List<VoucherSeckill> listSeckillVoucherInfo(SeckillVoucherInfoListRequest seckillVoucherInfoListRequest) {
        //todo 管理员全局搜索
        return null;
    }

    @Override
    public Page<VoucherSeckillVO> listSeckillVoucherInfoByPage(
            SeckillVoucherInfoListByPageRequest seckillVoucherInfoListByPageRequest) {
        //todo 分页查询
        return null;
    }
}




