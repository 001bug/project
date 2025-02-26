package com.apihub.interfaceInfo.controller;


import com.apihub.common.common.BaseResponse;
import com.apihub.common.common.DeleteRequest;
import com.apihub.common.common.ErrorCode;
import com.apihub.common.common.ResultUtils;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.UserHolder;
import com.apihub.interfaceInfo.model.domain.InterfaceInfo;
import com.apihub.interfaceInfo.model.dto.InterfaceInfoAddRequest;
import com.apihub.interfaceInfo.model.dto.InterfaceInfoIdRequest;
import com.apihub.interfaceInfo.model.dto.InterfaceInfoQueryRequest;
import com.apihub.interfaceInfo.model.enums.InterfaceInfoStatusEnum;
import com.apihub.interfaceInfo.model.vo.InterfaceInfoVO;
import com.apihub.interfaceInfo.openFeign.client.InterfaceInfoClient;
import com.apihub.interfaceInfo.service.InterfaceInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

import static com.apihub.common.common.ErrorCode.OPERATION_ERROR;

@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
@RequiredArgsConstructor
public class InterfaceController {

    private final InterfaceInfoClient interfaceInfoClient;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    // region 增删改查
    @ApiOperation("添加接口信息（用户）")
    @PostMapping("/add/self")
    public BaseResponse<Boolean> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {

        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        //获取用户id，并且存储数据
        interfaceInfoService.saveInterfaceInfo(interfaceInfo, request);

        return ResultUtils.success(true);
    }

    @ApiOperation("根据id搜索接口信息")
    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        return ResultUtils.success(interfaceInfo);
    }

    @ApiOperation("获取列表（管理员）")
    @GetMapping("/list")
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        if (!interfaceInfoClient.checkAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "不是管理员");
        }
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }

        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
        return ResultUtils.success(interfaceInfoList);
    }

    @ApiOperation("分页获取列表")
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfoVO>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Integer topPrice = interfaceInfoQueryRequest.getTopPrice();
        Integer lowPrice = interfaceInfoQueryRequest.getLowPrice();
        if (topPrice == null) {
            topPrice = 999999;
        }
        if (lowPrice == null) {
            lowPrice = 0;
        }
        if (lowPrice < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

//        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
//        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        //判断interfaceInfoQueryRequest.getCurrent()是否为空
        Long current = interfaceInfoQueryRequest.getCurrent();
        Long size = interfaceInfoQueryRequest.getPageSize();
        if (current == null) {
            current = 1L;
        }
        if (size == null) {
            size = 10L;
        }
        // 限制爬虫
        if (size > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();


        Long id = interfaceInfoQueryRequest.getId();
        String name = interfaceInfoQueryRequest.getName();
        String description = interfaceInfoQueryRequest.getDescription();
        String url = interfaceInfoQueryRequest.getUrl();
        String requestHeader = interfaceInfoQueryRequest.getRequestHeader();
        String responseHeader = interfaceInfoQueryRequest.getResponseHeader();
        Integer status = interfaceInfoQueryRequest.getStatus();
        String method = interfaceInfoQueryRequest.getMethod();
        Long userId = interfaceInfoQueryRequest.getUserId();
        String category = interfaceInfoQueryRequest.getCategory();


        // description 需支持模糊搜索
        //interfaceInfoQuery.setDescription(null);

        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        //description 模糊搜索
        queryWrapper
                .like(StringUtils.isNotBlank(description), "description", description)
                //在上限价格和下限价格中查找
                .between("price", lowPrice, topPrice)
                .orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals("ascend"), sortField)
                //排除空参数
                .eq(ObjectUtils.isNotEmpty(id), "id", id)
                .eq(StringUtils.isNotEmpty(name), "name", name)
                .like(StringUtils.isNotEmpty(url), "url", url)
                .like(StringUtils.isNotEmpty(requestHeader), "requestHeader", requestHeader)
                .like(StringUtils.isNotEmpty(responseHeader), "responseHeader", responseHeader)
                .eq(status != null, "status", status)
                .eq(StringUtils.isNotEmpty(method), "method", method)
                .eq(userId != null, "userId", userId)
                .eq(StringUtils.isNotEmpty(category), "category", category);

        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
        Page<InterfaceInfoVO> interfaceInfoVOPage = (Page<InterfaceInfoVO>) interfaceInfoPage.convert(interfaceInfo -> {
            InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
            BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
            return interfaceInfoVO;
        });
        return ResultUtils.success(interfaceInfoVOPage);
    }

    @ApiOperation("接口发布")
    @PostMapping("/online")
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody InterfaceInfoIdRequest idRequest) {

        // 仅本人或管理员可修改
        Long userId = UserHolder.getUser();

        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long id = idRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 仅本人或管理员可修改
        if (!Objects.equals(userId, oldInterfaceInfo.getUserId()) && !interfaceInfoClient.checkAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 判断该接口是否可以调用
//        com.yupi.yuapiclientsdk.model.User user = new com.yupi.yuapiclientsdk.model.User();
//        user.setUsername("test");
//        String username = yuApiClient.getUsernameByPost(user);
//        if (StringUtils.isBlank(username)) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口验证失败");
//        }

        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }


    @ApiOperation("接口下线")
    @PostMapping("/offline")
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody InterfaceInfoIdRequest idRequest) {


        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }


        long id = idRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        Long userId = UserHolder.getUser();
        if (!Objects.equals(userId, oldInterfaceInfo.getUserId()) && !interfaceInfoClient.checkAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    @ApiOperation("接口删除")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest) {

        Long userId = UserHolder.getUser();

        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(deleteRequest.getId());

        // 仅本人或管理员可修改
        if (!Objects.equals(interfaceInfo.getUserId(), userId) && !interfaceInfoClient.checkAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(deleteRequest.getId());
        if (b) {
            return ResultUtils.success(true);
        } else {
            return new BaseResponse<>(OPERATION_ERROR);
        }
    }
}
