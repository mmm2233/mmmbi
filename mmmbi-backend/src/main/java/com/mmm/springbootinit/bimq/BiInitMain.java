package com.mmm.springbootinit.bimq;

import org.springframework.amqp.core.*;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static com.mmm.springbootinit.bimq.BiMqConstant.*;

/**
 * 创建交换机和队列
 * @Author：mmm
 * @Package：com.mmm.springbootinit.bimq
 * @Project：mmmbi-backend
 * @name：BiInitMain
 * @Date：2024/3/7 19:08
 * @Filename：BiInitMain
 */
//todo 还没测试过死信队列，与重试ai
@Configuration
public class BiInitMain {


//    public static void main(String[] args) {
//        try {
//            ConnectionFactory factory =new ConnectionFactory();
//            factory.setHost("localhost");
//            Connection connection = factory.newConnection();
//            Channel channel = connection.createChannel();
//
//            String EXCHANGE_NAME = BI_EXCHANGE_NAME;
//            channel.exchangeDeclare(BI_EXCHANGE_NAME,"direct");
//            // 死信交换机
//            String EXCHANGE_DEAD = BI_EXCHANGE_DEAD;
//            channel.exchangeDeclare(EXCHANGE_DEAD,"direct");
//
//            //创建队列
//            String queueName = BI_QUEUE_NAME;
//            //通过设置 x-message-ttl 参数来指定消息的过期时间
//            HashMap<String, Object> queueArgs = new HashMap<>();
//            queueArgs.put("x-message-ttl",600000);
//            channel.queueDeclare(queueName,true,false,false,queueArgs);
//            channel.queueBind(queueName,EXCHANGE_NAME,BiMqConstant.BI_ROUTING_KEY);
//
//            //空字符串表示所有死信都会到这
//            String deadLetterRoutingKey ="";
//            HashMap<String, Object> deadArgs = new HashMap<>();
//            deadArgs.put("x-dead-letter-exchange",BI_EXCHANGE_DEAD);
//            deadArgs.put("x-dead-letter-routihg-key",deadLetterRoutingKey);
//            String queuedeadName =BI_QUEUE_DEAD;
//            channel.queueDeclare(queuedeadName,true,false,false,null);
//            channel.queueBind(queuedeadName,BI_EXCHANGE_DEAD,"");
//
//            // 创建缓存队列交换机等
//            // todo 按道理说这里消息失败还应该有个死信队列处理，以后写吧
//            String EXCHANGE_CACHE = BI_EXCHANGE_CACHE;
//            channel.exchangeDeclare(EXCHANGE_CACHE,"direct");
//            String queueCache = BI_QUEUE_CACHE;
//            channel.queueDeclare(queueCache,true,false,false,null);
//            channel.queueBind(queueCache,BI_EXCHANGE_CACHE,BI_ROUTING_CACHE);
//
//        } catch (TimeoutException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    /*
        声明死信交换机和队列
     */
    @Bean
    Queue BiDeadQueue(){
        return QueueBuilder.durable(BI_QUEUE_DEAD).build();
    }

    @Bean
    DirectExchange BiDeadExchange(){
        return new DirectExchange(BI_EXCHANGE_DEAD);
    }

    @Bean
    Binding BiDeadBinding(Queue BiDeadQueue, DirectExchange BiDeadExchange) {
        return BindingBuilder.bind(BiDeadQueue).to(BiDeadExchange).with(BI_ROUTING_DEAD);
    }

    /*
        声明缓存
     */
    @Bean
    Queue BiCacheQueue(){
        HashMap<String, Object> arg = new HashMap<>();
        arg.put("x-message-ttl",60000);

        //绑定死信交换机
        arg.put("x-dead-letter-exchange", BI_EXCHANGE_DEAD);
        arg.put("x-dead-letter-routing-key", BI_ROUTING_DEAD);

        return QueueBuilder.durable(BI_QUEUE_CACHE).withArguments(arg).build();
    }

    @Bean
    DirectExchange BiCacheExchange(){
        return new DirectExchange(BI_EXCHANGE_CACHE);
    }

    @Bean
    Binding BiCacheBinding(Queue BiCacheQueue, DirectExchange BiCacheExchange) {
        return BindingBuilder.bind(BiCacheQueue).to(BiCacheExchange).with(BI_ROUTING_CACHE);
    }

    /*
        声明图表
     */
    @Bean
    Queue BiChartQueue(){
        HashMap<String, Object> arg = new HashMap<>();
        arg.put("x-message-ttl",60000);

        //绑定死信交换机
        arg.put("x-dead-letter-exchange", BI_EXCHANGE_DEAD);
        arg.put("x-dead-letter-routing-key", BI_ROUTING_DEAD);
        return QueueBuilder.durable(BI_QUEUE_NAME).withArguments(arg).build();
    }

    @Bean
    DirectExchange BiChartExchange(){
        return new DirectExchange(BI_EXCHANGE_NAME);
    }

    @Bean
    Binding BiChartBinding(Queue BiChartQueue, DirectExchange BiChartExchange) {
        return BindingBuilder.bind(BiChartQueue).to(BiChartExchange).with(BI_ROUTING_KEY);
    }
}
