package com.apihub.voucher.service.impl;

import cn.hutool.core.util.IdUtil;
import com.apihub.common.common.ErrorCode;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.UserHolder;
import com.apihub.voucher.mapper.VoucherInfoMapper;
import com.apihub.voucher.model.dto.seckillinfo.SeckillVoucherInfoAddRequest;
import com.apihub.voucher.model.dto.voucherinfo.*;
import com.apihub.voucher.model.entity.VoucherInfo;
import com.apihub.voucher.model.entity.VoucherSeckill;
import com.apihub.voucher.model.otherInfo.VoucherInfoOtherInfo;
import com.apihub.voucher.model.vo.VoucherInfoVO;
import com.apihub.voucher.openFeign.client.UserServiceClient;
import com.apihub.voucher.service.VoucherInfoService;
import com.apihub.voucher.service.VoucherSeckillService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

import static com.apihub.voucher.utils.VoucherInfoConstant.*;

/**
 * @author IKUN
 * @description 针对表【voucher_info】的数据库操作Service实现
 * @createDate 2023-11-22 20:48:15
 */
@Service
@RequiredArgsConstructor
public class VoucherInfoServiceImpl extends ServiceImpl<VoucherInfoMapper, VoucherInfo>
        implements VoucherInfoService {
    private final UserServiceClient userServiceClient;

    @Resource
    private VoucherSeckillService voucherSeckillService;

    @Resource
    private VoucherInfoMapper voucherInfoMapper;

    //仅普通优惠券信息添加
    @Override
    public void saveCommonVoucherInfo(VoucherInfoAddRequest voucherInfoAddRequest, HttpServletRequest request) {
        // 检查优惠券类型是否为普通优惠券类型
        if (!Objects.equals(voucherInfoAddRequest.getType(), TYPE_BALANCE)
                && !Objects.equals(voucherInfoAddRequest.getType(), TYPE_INTERFACE)) {
            // 如果不是普通优惠券类型，抛出业务异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不是普通优惠券类型");
        }

        // 创建一个新的VoucherInfoAdd对象
        VoucherInfoAdd voucherInfoAdd = new VoucherInfoAdd();

        // 将请求对象中的属性复制到VoucherInfoAdd对象中
        BeanUtils.copyProperties(voucherInfoAddRequest, voucherInfoAdd);

        // 保存优惠券信息
        saveVoucherInfo(voucherInfoAdd);
    }


    //包含普通优惠券和秒杀优惠券的信息添加
    private String saveVoucherInfo(VoucherInfoAdd voucherInfoAdd) {
        //数据校验
        if (voucherInfoAdd == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = voucherInfoAdd.getTitle();
        if (StringUtils.isAnyBlank(title)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请填写标题");
        }
        if (title.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "优惠券标题过长");
        }
        Long payValue = voucherInfoAdd.getPayValue();
        Long actualValue = voucherInfoAdd.getActualValue();
        if (payValue == null || actualValue == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (payValue >= actualValue) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "优惠券无优惠额度");
        }


        //只有管理员才能添加
        if (!userServiceClient.checkAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        VoucherInfo voucherInfoSave = new VoucherInfo();
        BeanUtils.copyProperties(voucherInfoAdd, voucherInfoSave);

        //如果没有设置优惠券提供者，则默认为设置人员
        if (voucherInfoSave.getIssuerId() == null) voucherInfoSave.setIssuerId(UserHolder.getUser());
        //生成唯一兑换码,32位
        voucherInfoSave.setActivationCode(IdUtil.fastSimpleUUID());
        //otherInfo
        if (Objects.equals(voucherInfoAdd.getType(), TYPE_INTERFACE)
                || Objects.equals(voucherInfoAdd.getType(), TYPE_SECKILL_INTERFACE)) {
            if (voucherInfoAdd.getValidTime() == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "时段卡需要填写可用时长");
            }
            VoucherInfoOtherInfo voucherInfoOtherInfo = new VoucherInfoOtherInfo();
            voucherInfoOtherInfo.setValidTime(voucherInfoAdd.getValidTime());
            Gson gson = new Gson();
            voucherInfoSave.setOtherInfo(gson.toJson(voucherInfoOtherInfo));
        }

        voucherInfoSave.setStatus(STATUS_DOWN);

        boolean save = this.save(voucherInfoSave);
        if (!save) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }

        //todo 可以通过返回id来简化操作,而不是通过优惠券兑换码
        //todo 支持优惠券兑换码自定义，同时检查优惠券兑换码是否唯一
        //return voucherInfoSave.getId();
        return voucherInfoSave.getActivationCode();
    }


    @Override
    public void saveSeckillVoucherInfo(SeckillVoucherInfoAddRequest seckillVoucherInfoAddRequest, HttpServletRequest request) {
        Integer seckillVoucherInfoAddRequestType = seckillVoucherInfoAddRequest.getType();
        if (!Objects.equals(seckillVoucherInfoAddRequestType, TYPE_SECKILL_BALANCE)
                && !Objects.equals(seckillVoucherInfoAddRequestType, TYPE_SECKILL_INTERFACE)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不是秒杀券类型");
        }
        //添加优惠券信息
        VoucherInfoAdd voucherInfoAdd = new VoucherInfoAdd();
        BeanUtils.copyProperties(seckillVoucherInfoAddRequest, voucherInfoAdd);
        String activationCode = this.saveVoucherInfo(voucherInfoAdd);

        //添加秒杀库存信息
        VoucherSeckill voucherSeckill = new VoucherSeckill();
        if (seckillVoucherInfoAddRequest.getStock() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请填写库存");
        }
        voucherSeckill.setStock(seckillVoucherInfoAddRequest.getStock());
        if (seckillVoucherInfoAddRequest.getBeginTime() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请填写优惠券秒杀开始时间");
        }
        voucherSeckill.setBeginTime(seckillVoucherInfoAddRequest.getBeginTime());
        if (seckillVoucherInfoAddRequest.getEndTime() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请填写优惠券秒杀过期时间");
        }
        voucherSeckill.setEndTime(seckillVoucherInfoAddRequest.getEndTime());

        //查询voucherInfo的id，并存储
        QueryWrapper<VoucherInfo> voucherInfoQueryWrapper = new QueryWrapper<>();
        voucherInfoQueryWrapper.eq("activationCode", activationCode);
        VoucherInfo voucherInfo = voucherInfoMapper.selectOne(voucherInfoQueryWrapper);
        Long voucherInfoId = voucherInfo.getId();
        voucherSeckill.setVoucherId(voucherInfoId);

        voucherSeckillService.save(voucherSeckill);

        //查询voucherSeckillInfo的id，并存入到voucherInfo的seckillId
        Long voucherSeckillId = voucherSeckillService.query().eq("voucherId", voucherInfoId).one().getId();
        //将seckillId保存到voucherInfo中
        VoucherInfo voucherInfo1 = new VoucherInfo();
        voucherInfo1.setId(voucherInfoId);
        voucherInfo1.setSeckillId(voucherSeckillId);
        this.updateById(voucherInfo1);
    }

    @Override
    public void delVoucherInfo(VoucherInfoDelRequest voucherInfoDelRequest) {
        //只有管理员才能删除
        if (!userServiceClient.checkAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        Long id = voucherInfoDelRequest.getId();
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //删除秒杀信息（如果有）
        Long seckillId = this.query().eq("id", id).one().getSeckillId();
        if (seckillId != null) {
            VoucherInfoDelRequest voucherInfoDelRequest1 = new VoucherInfoDelRequest();
            voucherInfoDelRequest1.setId(seckillId);
            voucherSeckillService.delSeckillVoucherInfo(voucherInfoDelRequest1);
        }
        //删除基本信息
        boolean b = this.removeById(id);
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }

    @Override
    public void updateVoucherInfo(VoucherInfoUpdateRequest voucherInfoUpdateRequest) {
        //只有管理员才能添加
        if (!userServiceClient.checkAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        VoucherInfo updateVoucherInfo = new VoucherInfo();
        BeanUtils.copyProperties(voucherInfoUpdateRequest, updateVoucherInfo);
        boolean b = this.updateById(updateVoucherInfo);
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }

    @Override
    public List<VoucherInfo> listVoucherInfo(VoucherInfoListRequest voucherInfoListRequest) {
        if (!userServiceClient.checkAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        VoucherInfo voucherInfoQuery = new VoucherInfo();
        Long payValueTop = null;
        Long payValueFloor = null;
        Long actualValueTop = null;
        Long actualValueFloor = null;

        if (voucherInfoListRequest != null) {
            BeanUtils.copyProperties(voucherInfoListRequest, voucherInfoQuery);

            payValueTop = voucherInfoListRequest.getPayValueTop();
            payValueFloor = voucherInfoListRequest.getPayValueFloor();
            actualValueTop = voucherInfoListRequest.getActualValueTop();
            actualValueFloor = voucherInfoListRequest.getActualValueFloor();
        }
        if (payValueTop == null) payValueTop = 99999999L;
        if (payValueFloor == null) payValueFloor = 0L;
        if (payValueTop < payValueFloor) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "payValue参数错误");
        }
        if (actualValueTop == null) actualValueTop = 99999999L;
        if (actualValueFloor == null) actualValueFloor = 0L;
        if (actualValueTop < actualValueFloor) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "actualValue参数错误");
        }

        QueryWrapper<VoucherInfo> voucherInfoQueryWrapper = new QueryWrapper<>(voucherInfoQuery);
        voucherInfoQueryWrapper.between("payValue", payValueFloor, payValueTop);
        voucherInfoQueryWrapper.between("actualValue", actualValueFloor, actualValueTop);


        return this.list(voucherInfoQueryWrapper);
    }

    @Override
    public Page<VoucherInfoVO> listVoucherInfoByPage(VoucherInfoListByPageRequest voucherInfoListByPageRequest) {
        VoucherInfo voucherInfoQuery = new VoucherInfo();
        long current = voucherInfoListByPageRequest.getCurrent();
        long pageSize = voucherInfoListByPageRequest.getPageSize();
        // 限制爬虫
        if (pageSize > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String sortOrder = voucherInfoListByPageRequest.getSortOrder();
        String sortField = voucherInfoListByPageRequest.getSortField();

        BeanUtils.copyProperties(voucherInfoListByPageRequest, voucherInfoQuery);

        Long payValueTop = voucherInfoListByPageRequest.getPayValueTop();
        Long payValueFloor = voucherInfoListByPageRequest.getPayValueFloor();
        Long actualValueTop = voucherInfoListByPageRequest.getActualValueTop();
        Long actualValueFloor = voucherInfoListByPageRequest.getActualValueFloor();
        if (payValueTop == null) payValueTop = 99999999L;
        if (payValueFloor == null) payValueFloor = 0L;
        if (payValueTop < payValueFloor) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "payValue参数错误");
        }
        if (actualValueTop == null) actualValueTop = 99999999L;
        if (actualValueFloor == null) actualValueFloor = 0L;
        if (actualValueTop < actualValueFloor) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "actualValue参数错误");
        }

        //需要模糊搜索
        String title = voucherInfoListByPageRequest.getTitle();
        String subTitle = voucherInfoListByPageRequest.getSubTitle();
        String rules = voucherInfoListByPageRequest.getRules();
        voucherInfoQuery.setTitle(null);
        voucherInfoQuery.setSubTitle(null);
        voucherInfoQuery.setRules(null);

        QueryWrapper<VoucherInfo> voucherInfoQueryWrapper = new QueryWrapper<>(voucherInfoQuery);
        voucherInfoQueryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        voucherInfoQueryWrapper.like(StringUtils.isNotBlank(title), "subTitle", subTitle);
        voucherInfoQueryWrapper.like(StringUtils.isNotBlank(title), "rules", rules);
        voucherInfoQueryWrapper.between("payValue", payValueFloor, payValueTop);
        voucherInfoQueryWrapper.between("actualValue", actualValueFloor, actualValueTop);
        voucherInfoQueryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals("ascend"), sortField);

        Page<VoucherInfo> voucherInfoPage = this.page(new Page<>(current, pageSize), voucherInfoQueryWrapper);

        Page<VoucherInfoVO> voucherInfoVOPage = (Page<VoucherInfoVO>) voucherInfoPage.convert(voucherInfo -> {
            VoucherInfoVO voucherInfoVO = new VoucherInfoVO();
            BeanUtils.copyProperties(voucherInfo, voucherInfoVO);
            return voucherInfoVO;
        });
        return voucherInfoVOPage;
    }
}




