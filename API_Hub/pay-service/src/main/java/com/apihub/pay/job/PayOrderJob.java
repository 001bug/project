package com.apihub.pay.job;


import com.apihub.pay.model.entity.ApiOrder;
import com.apihub.pay.model.entity.PayOrder;
import com.apihub.pay.service.ApiOrderService;
import com.apihub.pay.service.PayOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;

import static com.apihub.pay.model.enums.OrderStatus.NOT_GENERATED_PAID;
import static com.apihub.pay.model.enums.PayStatus.TRADE_SUCCESS;

/**
 * 缓存预热任务 ，开发时可用在 test 里代替
 */
@Component
@Slf4j
public class PayOrderJob {


    @Resource
    private PayOrderService payOrderService;

    @Resource
    private ApiOrderService apiOrderService;

    // cron   日期格式:秒 分 时 日 月 年
    @Scheduled(cron = "10 59 23 * * *")
    public void Job() {
        log.info("定时任务开启");
        //查询订单
        ApiOrder apiOrder = new ApiOrder();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);
        apiOrder.setOtherInfo(calendar.getTime().toString());
        //选择已经支付但是没有扣款的
        apiOrder.setStatus(NOT_GENERATED_PAID.getCode());

        QueryWrapper<ApiOrder> queryWrapper = new QueryWrapper<>(apiOrder);
        List<ApiOrder> apiOrderList = apiOrderService.list(queryWrapper);

        //生成订单
        for (ApiOrder order : apiOrderList) {
            PayOrder payOrder = new PayOrder();
            payOrder.setBizOrderNo(order.getId());
            payOrder.setBizUserId(order.getUserId());
            payOrder.setAmount(order.getTotalFee());
            payOrder.setCreater(order.getUserId());
            payOrder.setStatus(TRADE_SUCCESS.getValue());
            payOrder.setPayOverTime(calendar.getTime());
            payOrder.setPaySuccessTime(order.getUpdateTime());

            payOrderService.save(payOrder);
        }
    }
}
