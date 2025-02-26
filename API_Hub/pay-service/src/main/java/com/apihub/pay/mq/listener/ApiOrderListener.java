package com.apihub.pay.mq.listener;

import com.apihub.pay.model.dto.DeductOrderMqDTO;
import com.apihub.pay.model.dto.order.DeductOrderDTO;
import com.apihub.pay.service.ApiOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class ApiOrderListener {

    @Resource
    private ApiOrderService apiOrderService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "apiHub.payService.order.queue", durable = "true"),
            exchange = @Exchange(name = "payService.topic", type = ExchangeTypes.TOPIC),
            key = "order.success"
    ))
    public void deductOrder(DeductOrderMqDTO deductOrderMqDTO) {
        log.info("收到队列消息");
        DeductOrderDTO deductOrderDTO = new DeductOrderDTO();
        BeanUtils.copyProperties(deductOrderMqDTO,deductOrderDTO);
        apiOrderService.deductOrder(deductOrderDTO, deductOrderMqDTO.getUserId());
    }
}
