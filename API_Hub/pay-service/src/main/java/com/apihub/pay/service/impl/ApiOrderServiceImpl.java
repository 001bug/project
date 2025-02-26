package com.apihub.pay.service.impl;

import com.apihub.common.common.ErrorCode;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.ThrowUtils;
import com.apihub.pay.mapper.ApiOrderMapper;
import com.apihub.pay.model.dto.order.ApiOrderQueryRequest;
import com.apihub.pay.model.dto.order.DeductOrderDTO;
import com.apihub.pay.model.entity.ApiOrder;
import com.apihub.pay.model.vo.ApiOrderVO;
import com.apihub.pay.service.ApiOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDto;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static com.apihub.pay.model.enums.OrderStatus.NOT_GENERATED_PAID;

/**
 * @author IKUN
 * @description 针对表【api_order】的数据库操作Service实现
 * @createDate 2023-11-07 13:15:27
 */
@Service
@Slf4j
public class ApiOrderServiceImpl extends ServiceImpl<ApiOrderMapper, ApiOrder>
        implements ApiOrderService {

    @Resource
    private ApiOrderMapper apiOrderMapper;

    @Override
    public Boolean deductOrder(DeductOrderDTO deductOrderDTO,Long userId) {
        //数据检查
        if (deductOrderDTO.getInterfaceId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口id为空");
        }
        if (deductOrderDTO.getNum() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口id使用次数为空");
        }
        if (deductOrderDTO.getTotalFee() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "总费用为空");
        }
        //查询订单
        QueryWrapper<ApiOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("interfaceId", deductOrderDTO.getInterfaceId());
        queryWrapper.eq("userAddress", deductOrderDTO.getUserAddress());

        //todo 下面筛选endTime不知道为啥不行
        //queryWrapper.le("endTime",newCalendar.getTime());

        Calendar newCalendar = Calendar.getInstance();
        //直接选择今天的日期，指定时间，直接通过string比较
        newCalendar.set(Calendar.MINUTE, 59);
        newCalendar.set(Calendar.HOUR_OF_DAY, 23);
        newCalendar.set(Calendar.SECOND, 0);
        queryWrapper.eq("otherInfo", newCalendar.getTime().toString());

        ApiOrder oldOrder = apiOrderMapper.selectOne(queryWrapper);
        //查询到订单，修改数据
        if (oldOrder != null) {
            oldOrder.setTotalFee(oldOrder.getTotalFee() + deductOrderDTO.getTotalFee());
            oldOrder.setNum(oldOrder.getNum() + deductOrderDTO.getNum());
            return this.updateById(oldOrder);
        }
        //没查询到订单，新建订单
        ApiOrder newOrder = new ApiOrder();
        newOrder.setInterfaceId(deductOrderDTO.getInterfaceId());
        newOrder.setUserId(userId);
        newOrder.setNum(deductOrderDTO.getNum());
        newOrder.setPaymentType(deductOrderDTO.getPaymentType());
        newOrder.setTotalFee(deductOrderDTO.getTotalFee());

        newOrder.setStatus(NOT_GENERATED_PAID.getCode());

        //选择今天的日期，指定时间，以string形式存储
        newOrder.setOtherInfo(newCalendar.getTime().toString());

        newOrder.setPaymentType(3);
        //用户ip,可能是最后转发站的ip
        newOrder.setUserAddress(deductOrderDTO.getUserAddress());

        return this.save(newOrder);
    }

    @Override
    public Page<ApiOrderVO> listPayOrderByPage(ApiOrderQueryRequest apiOrderQueryRequest) {
        long current = apiOrderQueryRequest.getCurrent();
        long size = apiOrderQueryRequest.getPageSize();

        Integer maxFee = apiOrderQueryRequest.getMaxFee();
        Integer minFee = apiOrderQueryRequest.getMinFee();


        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        ApiOrder apiOrderQuery = new ApiOrder();
        BeanUtils.copyProperties(apiOrderQueryRequest, apiOrderQuery);
        QueryWrapper<ApiOrder> queryWrapper = new QueryWrapper<>(apiOrderQuery);

        //费用区间查询
        if (maxFee != null) {
            //小于等于
            queryWrapper.le("totalFee", maxFee);
        }
        if (minFee != null) {
            if (maxFee != null && maxFee < minFee) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            //大于等于
            queryWrapper.ge("totalFee", minFee);
        }
        String sql = queryWrapper.getCustomSqlSegment();
        log.info(sql);

        Page<ApiOrder> apiOrders = this.page(new Page<>(current, size), queryWrapper);
        Page<ApiOrderVO> apiOrderVOPage = new PageDto<>(apiOrders.getCurrent(), apiOrders.getSize(), apiOrders.getTotal());
        List<ApiOrderVO> apiOrderVOS = apiOrders.getRecords().stream().map(apiOrder -> {
            ApiOrderVO apiOrderVO = new ApiOrderVO();
            BeanUtils.copyProperties(apiOrder, apiOrderVO);

            return apiOrderVO;
        }).collect(Collectors.toList());
        apiOrderVOPage.setRecords(apiOrderVOS);
        return apiOrderVOPage;
    }
}




