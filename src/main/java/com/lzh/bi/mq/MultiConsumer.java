package com.lzh.bi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

/**
 * @author lzh
 */
public class MultiConsumer {

    private static final String TASK_QUEUE_NAME = "multi_queue";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        final Connection connection = factory.newConnection();
        for (int i = 0; i < 2; i++) {
            final Channel channel = connection.createChannel();

            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            // 设置预取计数为 1，这样RabbitMQ就会在给消费者新消息之前等待先前的消息被确认
            channel.basicQos(1);

            int finalI = i;
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

                try {
                    System.out.println(" [x] Received '" + "编号:" + (finalI + 1) + ":" + message + "'");
                    Thread.sleep(20000);
                    // 消费成功，手动确认
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                } catch (InterruptedException e) {
                    // 消费失败
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
                } finally {
                    System.out.println(" [x] Done");
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };
            channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
            });
        }

    }
}
