package com.mmm.springbootinit.bimq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.bimq
 * @Project：mmmbi-backend
 * @name：MyMessageProducer
 * @Date：2024/3/7 18:31
 * @Filename：MyMessageProducer
 */


@Component
public class BiMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String message){
        rabbitTemplate.convertAndSend(BiMqConstant.BI_EXCHANGE_NAME,BiMqConstant.BI_ROUTING_KEY,message);
    }

    public void sendMessageCache(String message) {
        rabbitTemplate.convertAndSend(BiMqConstant.BI_EXCHANGE_CACHE,BiMqConstant.BI_ROUTING_CACHE,message);
    }
}
