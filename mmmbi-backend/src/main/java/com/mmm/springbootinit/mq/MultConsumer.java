package com.mmm.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.mq
 * @Project：mmmbi-backend
 * @name：MultConsumer
 * @Date：2024/3/7 9:07
 * @Filename：MultConsumer
 */
public class MultConsumer {
    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] argv) throws Exception {
        // 建立连接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        final Connection connection = factory.newConnection();

        for(int i =0;i<2;i++){
            final Channel channel = connection.createChannel();
            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            // 控制单个消费者处理任务的积压数
            channel.basicQos(1);

            int finalI =i+1;
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");

                try {
                    System.out.println(" [x] Received '" +"编号"+ finalI + "：" + message + "'");
                    // 模拟处理业务
                    Thread.sleep(20000);
                    // 确认某条消息，第一个参数获取都某个消息，第二个参数批量确认，一次性确认所有历史消息直到目前这条
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println(" [x] Done");
                    //指定拒绝某条消息，第三个参数代表是否需要重新入队，可以用于重试，其它参数同上
                    //channel.basicNack();
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };
            // 消息确认机制，为了保证消费成功被消费，rmq提供了消息确认机制，当消费者接收消息后，发送一个反馈才会方向的移除消息
            // ack:成功、nack:失败、reject:拒绝
            // autoAck false 会自动执行ack命令收到消息后立刻成功
            channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> { });
        }

    }

}
