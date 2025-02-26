package com.apihub.voucher.service;

import com.apihub.voucher.model.dto.seckillinfo.SeckillVoucherInfoListByPageRequest;
import com.apihub.voucher.model.dto.seckillinfo.SeckillVoucherInfoListRequest;
import com.apihub.voucher.model.dto.seckillinfo.SeckillVoucherInfoUpdateRequest;
import com.apihub.voucher.model.dto.voucherinfo.VoucherInfoDelRequest;
import com.apihub.voucher.model.entity.VoucherSeckill;
import com.apihub.voucher.model.vo.VoucherSeckillVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author IKUN
 * @description 针对表【voucher_seckill(秒杀优惠券表)】的数据库操作Service
 * @createDate 2023-11-22 20:48:34
 */
public interface VoucherSeckillService extends IService<VoucherSeckill> {

    void delSeckillVoucherInfo(VoucherInfoDelRequest voucherInfoDelRequest);

    void updateSeckillVoucherInfo(SeckillVoucherInfoUpdateRequest seckillVoucherInfoUpdateRequest);

    List<VoucherSeckill> listSeckillVoucherInfo(SeckillVoucherInfoListRequest seckillVoucherInfoListRequest);

    Page<VoucherSeckillVO> listSeckillVoucherInfoByPage(SeckillVoucherInfoListByPageRequest seckillVoucherInfoListByPageRequest);

    VoucherSeckillVO getSeckillVoucherInfoById(Long id);
}
