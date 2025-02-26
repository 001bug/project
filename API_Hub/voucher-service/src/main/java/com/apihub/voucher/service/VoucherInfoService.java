package com.apihub.voucher.service;

import com.apihub.voucher.model.dto.seckillinfo.SeckillVoucherInfoAddRequest;
import com.apihub.voucher.model.dto.voucherinfo.*;
import com.apihub.voucher.model.entity.VoucherInfo;
import com.apihub.voucher.model.vo.VoucherInfoVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author IKUN
 * @description 针对表【voucher_info】的数据库操作Service
 * @createDate 2023-11-22 20:48:15
 */
public interface VoucherInfoService extends IService<VoucherInfo> {

    void saveCommonVoucherInfo(VoucherInfoAddRequest voucherInfoAddRequest, HttpServletRequest request);

    void delVoucherInfo(VoucherInfoDelRequest voucherInfoDelRequest);

    void updateVoucherInfo(VoucherInfoUpdateRequest voucherInfoUpdateRequest);

    List<VoucherInfo> listVoucherInfo(VoucherInfoListRequest voucherInfoListRequest);

    Page<VoucherInfoVO> listVoucherInfoByPage(VoucherInfoListByPageRequest voucherInfoListByPageRequest);

    void saveSeckillVoucherInfo(SeckillVoucherInfoAddRequest seckillVoucherInfoAddRequest, HttpServletRequest request);
}
