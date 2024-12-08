package com.lzh.bi.mq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class TestMessageProducer {

    @Resource
    private MessageProducer messageProducer;

    @Test
    void sendMessage() {
        messageProducer.sendMessage("哈哈哈");
    }
}