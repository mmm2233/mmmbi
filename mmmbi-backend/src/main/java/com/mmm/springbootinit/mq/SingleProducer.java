package com.mmm.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.mq
 * @Project：mmmbi-backend
 * @name：SingleProducer
 * @Date：2024/3/6 23:37
 * @Filename：SingleProducer
 */
public class SingleProducer {
    private final static String QUEUE_NAME = "hello";
    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
            // 频道提供了和消息队列server建立通信的传输方法(为了复用连接，提高传输效率)，程序通过Channel操作rabbmiq
            Channel channel = connection.createChannel()) {
            // 创建消息队列 durable是否持久化，exclusive是否只运行当前这个创建消息队列的连接操作，autoDelete没人用队列是否要删除队列
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello World wooowoowowowowoow";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }

    }
}
