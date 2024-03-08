package com.mmm.springbootinit.bimq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static com.mmm.springbootinit.bimq.BiMqConstant.*;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.bimq
 * @Project：mmmbi-backend
 * @name：BiInitMain
 * @Date：2024/3/7 19:08
 * @Filename：BiInitMain
 */
public class BiInitMain {
    public static void main(String[] args) {
        try {
            ConnectionFactory factory =new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            String EXCHANGE_NAME = BI_EXCHANGE_NAME;
            channel.exchangeDeclare(BI_EXCHANGE_NAME,"direct");
            // 死信交换机
            String EXCHANGE_DEAD = BI_EXCHANGE_DEAD;
            channel.exchangeDeclare(EXCHANGE_DEAD,"direct");

            //创建队列
            String queueName = BI_QUEUE_NAME;
            //通过设置 x-message-ttl 参数来指定消息的过期时间
            HashMap<String, Object> queueArgs = new HashMap<>();
            queueArgs.put("x-message-ttl",600000);
            channel.queueDeclare(queueName,true,false,false,queueArgs);
            channel.queueBind(queueName,EXCHANGE_NAME,BiMqConstant.BI_ROUTING_KEY);

            //空字符串表示所有死信都会到这
            String deadLetterRoutingKey ="";
            HashMap<String, Object> deadArgs = new HashMap<>();
            deadArgs.put("x-dead-letter-exchange",BI_EXCHANGE_DEAD);
            deadArgs.put("x-dead-letter-routihg-key",deadLetterRoutingKey);
            String queuedeadName =BI_QUEUE_DEAD;
            channel.queueDeclare(queuedeadName,true,false,false,null);
            channel.queueBind(queuedeadName,BI_EXCHANGE_DEAD,"");
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
