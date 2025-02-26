package com.apihub.pay.service;

import com.apihub.pay.model.dto.order.ApiOrderQueryRequest;
import com.apihub.pay.model.dto.order.DeductOrderDTO;
import com.apihub.pay.model.entity.ApiOrder;
import com.apihub.pay.model.vo.ApiOrderVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author IKUN
* @description 针对表【api_order】的数据库操作Service
* @createDate 2023-11-07 13:15:27
*/
public interface ApiOrderService extends IService<ApiOrder> {

    Boolean deductOrder(DeductOrderDTO deductOrderDTO,Long userId);

    Page<ApiOrderVO> listPayOrderByPage(ApiOrderQueryRequest apiOrderQueryRequest);
}
