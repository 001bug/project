package com.apihub.interfaceInfo.service;

import com.apihub.interfaceInfo.model.domain.InterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author IKUN
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-10-24 00:38:35
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean b);

    void saveInterfaceInfo(InterfaceInfo interfaceInfo, HttpServletRequest request);
}
