package com.mmm.springbootinit.bimq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.bimq
 * @Project：mmmbi-backend
 * @name：MyMessageProducerTest
 * @Date：2024/3/7 18:58
 * @Filename：MyMessageProducerTest
 */
@SpringBootTest
class MyMessageProducerTest {

    @Resource
    private MyMessageProducer myMessageProducer;

    @Test
    void sendMessage() {
        myMessageProducer.sendMessage("code_exchange","my_routingKey","你哈");
    }
}