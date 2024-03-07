package com.mmm.springbootinit.mq;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.util.Scanner;


/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.mq
 * @Project：mmmbi-backend
 * @name：MultProducer
 * @Date：2024/3/7 9:06
 * @Filename：MultProducer
 */
public class MultProducer {
    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            // durable设置true，队列持久化，当服务器重启后队列不会丢失
            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);

            Scanner scanner = new Scanner(System.in);


            //String message = String.join(" ", argv);
            while (scanner.hasNext()) {
                String message = scanner.nextLine();

                // MessageProperties.PERSISTENT_TEXT_PLAIN,消息持久化
                channel.basicPublish("", TASK_QUEUE_NAME,
                        MessageProperties.PERSISTENT_TEXT_PLAIN,
                        message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + "'");
            }
        }
    }
}
