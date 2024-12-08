package com.lzh.bi.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author lzh
 */
@Component
@Slf4j
public class TestMessageConsumer {

    /*@SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MqConstant.BI_QUEUE),
            exchange = @Exchange(name = MqConstant.BI_EXCHANGE, type = ExchangeTypes.DIRECT),
            key = MqConstant.BI_ROUTING_KEY
    ), ackMode = "MANUAL")
    public void handleMessage(String message, Channel channel,
                              @Header(value = AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("received message = {}", message);
        channel.basicAck(deliveryTag, false);
    }*/
}
