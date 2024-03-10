package com.mmm.springbootinit.bimq.common;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 公共队列生产者
 * @Author：mmm
 * @Package：com.mmm.springbootinit.bimq.common
 * @Project：mmmbi-backend
 * @name：MqMessageProducer
 * @Date：2024/3/9 21:42
 * @Filename：MqMessageProducer
 */
@Component
public class MqMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发信息
     * @param exchange
     * @param routingKey
     * @param message
     */
    public void sendMessage(String exchange,String routingKey,String message){
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
    }
}
