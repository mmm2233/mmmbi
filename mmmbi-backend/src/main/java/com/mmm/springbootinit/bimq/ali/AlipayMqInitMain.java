package com.mmm.springbootinit.bimq.ali;

import com.mmm.springbootinit.bimq.BiMqConstant;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.bimq.ali
 * @Project：mmmbi-backend
 * @name：AlipayMqInitMain
 * @Date：2024/3/9 11:33
 * @Filename：AlipayMqInitMain
 */
@Configuration
public class AlipayMqInitMain {

    /**
     * 将死信队列和交换机声明
     */
    @Bean
    Queue AliPayDeadQueue(){
        return QueueBuilder.durable(BiMqConstant.ORDERS_DEAD_QUEUE_NAME).build();
    }

    @Bean
    DirectExchange AliPayDeadExchange() {
        return new DirectExchange(BiMqConstant.ORDERS_DEAD_EXCHANGE_NAME);
    }


    @Bean
    Binding AliPayDeadBinding(Queue AliPayDeadQueue, DirectExchange AliPayDeadExchange) {
        return BindingBuilder.bind(AliPayDeadQueue).to(AliPayDeadExchange).with(BiMqConstant.ORDERS_DEAD_ROUTING_KEY);
    }

    /**
     * 将队列和交换机声明
     */
    @Bean
    Queue AliPayQueue(){
        //信息参数 设置TTL为1min
        Map<String,Object> arg = new HashMap<>();
        arg.put("x-message-ttl",1000*5*60);
        //绑定死信交换机
        arg.put("x-dead-letter-exchange",BiMqConstant.ORDERS_DEAD_EXCHANGE_NAME);
        arg.put("x-dead-letter-routing-key",BiMqConstant.ORDERS_DEAD_ROUTING_KEY);
        return QueueBuilder.durable(BiMqConstant.ORDERS_QUEUE_NAME).withArguments(arg).build();
    }

    @Bean
    DirectExchange AliPayExchange() {
        return new DirectExchange(BiMqConstant.ORDERS_EXCHANGE_NAME);
    }

    @Bean
    Binding AliPayBinding(Queue AliPayQueue, DirectExchange AliPayExchange) {
        return BindingBuilder.bind(AliPayQueue).to(AliPayExchange).with(BiMqConstant.ORDERS_ROUTING_KEY);
    }
}
