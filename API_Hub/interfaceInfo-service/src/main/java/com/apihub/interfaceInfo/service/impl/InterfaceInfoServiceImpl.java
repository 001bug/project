package com.apihub.interfaceInfo.service.impl;

import com.apihub.common.common.ErrorCode;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.UserHolder;
import com.apihub.interfaceInfo.mapper.InterfaceInfoMapper;
import com.apihub.interfaceInfo.model.domain.InterfaceInfo;
import com.apihub.interfaceInfo.openFeign.client.InterfaceInfoClient;
import com.apihub.interfaceInfo.service.InterfaceInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author IKUN
 * @description 针对表【interface_info(接口信息)】的数据库操作Service实现
 * @createDate 2023-10-24 00:38:35
 */
@Service
@RequiredArgsConstructor
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService {

    private final InterfaceInfoClient interfaceInfoClient;

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean b) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        // 接口名字不能为空
        if (b) {
            if (StringUtils.isAnyBlank(name)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
        String category = interfaceInfo.getCategory();
        if (category == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口类别未填写");
        }
    }

    @Override
    public void saveInterfaceInfo(InterfaceInfo interfaceInfo, HttpServletRequest request) {
        //用于测试openFeign远程调用是否成功
//        UserVO userVO = interfaceInfoClient.getCurrentUser();
//        if (userVO == null || userVO.getId() == null){
//            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
//        }
        InterfaceInfo interfaceInfoSave = interfaceInfo;
        interfaceInfoSave.setUserId(UserHolder.getUser());
        boolean result = this.save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }


}




