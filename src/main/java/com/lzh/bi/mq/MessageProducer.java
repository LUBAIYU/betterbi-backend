package com.lzh.bi.mq;

import com.lzh.bi.constants.MqConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 消息生产者
 *
 * @author lzh
 */
@Component
public class MessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(MqConstant.BI_EXCHANGE, MqConstant.BI_ROUTING_KEY, message);
    }
}
