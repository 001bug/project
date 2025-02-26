package com.apihub.voucher.controller;


import com.apihub.common.common.BaseResponse;
import com.apihub.common.common.ResultUtils;
import com.apihub.voucher.model.dto.seckillinfo.SeckillVoucherInfoAddRequest;
import com.apihub.voucher.model.dto.seckillinfo.SeckillVoucherInfoListByPageRequest;
import com.apihub.voucher.model.dto.seckillinfo.SeckillVoucherInfoListRequest;
import com.apihub.voucher.model.dto.seckillinfo.SeckillVoucherInfoUpdateRequest;
import com.apihub.voucher.model.dto.voucherinfo.VoucherInfoDelRequest;
import com.apihub.voucher.model.entity.VoucherSeckill;
import com.apihub.voucher.model.vo.VoucherSeckillVO;
import com.apihub.voucher.service.VoucherInfoService;
import com.apihub.voucher.service.VoucherSeckillService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

//秒杀优惠券信息CRUD
@RestController
@Slf4j
@RequestMapping("/voucherInfo/seckill")
public class SeckillVoucherInfoController {

    @Resource
    private VoucherInfoService voucherInfoService;

    @Resource
    private VoucherSeckillService voucherSeckillService;

    //限量优惠券即秒杀优惠券
    @ApiOperation("添加秒杀优惠券信息")
    @PostMapping("/add")
    public BaseResponse<Boolean> addSeckillVoucherInfo(@RequestBody SeckillVoucherInfoAddRequest seckillVoucherInfoAddRequest,
                                                       HttpServletRequest request) {

        //委托给voucherInfoService进行处理
        voucherInfoService.saveSeckillVoucherInfo(seckillVoucherInfoAddRequest, request);

        return ResultUtils.success(true);
    }

    @ApiOperation("删除秒杀优惠券信息")
    @PostMapping("/del")
    public BaseResponse<Boolean> delSeckillVoucherInfo(@RequestBody VoucherInfoDelRequest voucherInfoDelRequest,
                                                       HttpServletRequest request) {
        //如果需要删除基本信息,请移步VoucherInfoController
        //此处仅删除秒杀信息
        voucherSeckillService.delSeckillVoucherInfo(voucherInfoDelRequest);
        return ResultUtils.success(true);
    }

    @ApiOperation("修改秒杀优惠券信息")
    @PostMapping("/update")
    public BaseResponse<Boolean> updateSeckillVoucherInfo(
            @RequestBody SeckillVoucherInfoUpdateRequest seckillVoucherInfoUpdateRequest,
            HttpServletRequest request) {

        voucherSeckillService.updateSeckillVoucherInfo(seckillVoucherInfoUpdateRequest);
        return ResultUtils.success(true);
    }

    @ApiOperation("根据id获取秒杀优惠券信息")
    @GetMapping("/get/{id}")
    public BaseResponse<VoucherSeckillVO> getSeckillVoucherInfoById(
            @PathVariable("id") Long id,
            HttpServletRequest request) {

        return ResultUtils.success(voucherSeckillService.getSeckillVoucherInfoById(id));
    }

    @ApiOperation("全局搜索秒杀优惠券信息（管理员）")
    @PostMapping("/list/admin")
    public BaseResponse<List<VoucherSeckill>> listVoucherInfo(
            @RequestBody SeckillVoucherInfoListRequest seckillVoucherInfoListRequest,
            HttpServletRequest request) {

        return ResultUtils.success(voucherSeckillService.listSeckillVoucherInfo(seckillVoucherInfoListRequest));
    }

    @ApiOperation("分页搜索秒杀优惠券信息")
    @PostMapping("/list/page")
    public BaseResponse<Page<VoucherSeckillVO>> listVoucherInfoByPage(
            @RequestBody SeckillVoucherInfoListByPageRequest seckillVoucherInfoListByPageRequest,
            HttpServletRequest request) {

        return ResultUtils.success(
                voucherSeckillService.listSeckillVoucherInfoByPage(seckillVoucherInfoListByPageRequest)
        );
    }
}
