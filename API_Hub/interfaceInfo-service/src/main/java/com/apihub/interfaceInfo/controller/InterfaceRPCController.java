package com.apihub.interfaceInfo.controller;


import com.apihub.common.common.ErrorCode;
import com.apihub.common.exception.BusinessException;
import com.apihub.interfaceInfo.model.domain.InterfaceInfo;
import com.apihub.interfaceInfo.service.InterfaceInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "InterfaceInfoRPC接口")
@RestController
@Slf4j
@RequestMapping("/interfaceInfoRPC")
public class InterfaceRPCController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @ApiOperation("根据id搜索接口信息")
    @GetMapping("/get")
    public InterfaceInfo getInterfaceInfoById(@RequestParam("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        //如果接口关闭，则返回空
        if (interfaceInfo.getStatus() == 0) return null;
        return interfaceInfo;
    }

}
