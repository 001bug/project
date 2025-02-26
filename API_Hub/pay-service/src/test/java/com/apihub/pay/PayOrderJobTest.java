package com.apihub.pay;

import com.apihub.pay.model.entity.ApiOrder;
import com.apihub.pay.model.entity.PayOrder;
import com.apihub.pay.service.ApiOrderService;
import com.apihub.pay.service.PayOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;

import static com.apihub.pay.model.enums.OrderStatus.NOT_GENERATED_PAID;
import static com.apihub.pay.model.enums.PayStatus.TRADE_SUCCESS;

@SpringBootTest
public class PayOrderJobTest {
    @Resource
    private PayOrderService payOrderService;

    @Resource
    private ApiOrderService apiOrderService;

    @Test
    public void TestJob() {
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

            payOrderService.save(payOrder);
        }
    }
}
