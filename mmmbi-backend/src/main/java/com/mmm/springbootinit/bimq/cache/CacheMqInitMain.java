package com.mmm.springbootinit.bimq.cache;

import com.mmm.springbootinit.constant.MqConstant;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.bimq.cache
 * @Project：mmmbi-backend
 * @name：CacheMqInitMain
 * @Date：2024/3/9 22:49
 * @Filename：CacheMqInitMain
 */
@Configuration
public class CacheMqInitMain {
    /*
       声明死信交换机和队列
    */
    @Bean
    Queue CacheDeadQueue(){
        return QueueBuilder.durable(MqConstant.CACHE_DEAD_QUEUE_NAME).build();
    }

    @Bean
    DirectExchange CacheDeadExchange(){
        return new DirectExchange(MqConstant.CACHE_DEAD_EXCHANGE_NAME);
    }

    @Bean
    Binding CacheDeadBinding(Queue CacheDeadQueue, DirectExchange CacheDeadExchange) {
        return BindingBuilder.bind(CacheDeadQueue).to(CacheDeadExchange).with(MqConstant.CACHE_DEAD_ROUTING_KEY);
    }

    /*
        声明缓存
     */
    @Bean
    Queue CacheQueue(){
        HashMap<String, Object> arg = new HashMap<>();
        arg.put("x-message-ttl",60000);

        //绑定死信交换机
        arg.put("x-dead-letter-exchange", MqConstant.CACHE_DEAD_EXCHANGE_NAME);
        arg.put("x-dead-letter-routing-key", MqConstant.CACHE_DEAD_ROUTING_KEY);

        return QueueBuilder.durable(MqConstant.CACHE_QUEUE_NAME).withArguments(arg).build();
    }

    @Bean
    DirectExchange CacheExchange(){
        return new DirectExchange(MqConstant.CACHE_EXCHANGE_NAME);
    }

    @Bean
    Binding CacheBinding(Queue CacheQueue, DirectExchange CacheExchange) {
        return BindingBuilder.bind(CacheQueue).to(CacheExchange).with(MqConstant.CACHE_ROUTING_KEY);
    }
}
