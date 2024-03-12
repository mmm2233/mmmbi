package com.mmm.springbootinit.bimq.chart;

import com.mmm.springbootinit.constant.MqConstant;
import org.springframework.amqp.core.*;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;


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
public class ChartMqInitMain {


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
    /**
     * durable：指示队列是否持久化。如果设置为true，则RabbitMQ会将队列保存到磁盘上，以便在服务器重启后恢复。如果设置为false，则消息仅存在于内存中，服务器重启时会丢失。默认为false。
     * exclusive：指示队列是否为专有队列。如果设置为true，则只有声明队列的连接可以使用该队列。一旦连接关闭，队列就会自动删除。默认为false。
     * autoDelete：指示队列是否为自动删除队列。如果设置为true，则队列在消费者断开连接时会自动删除。默认为false。
     * arguments：用于设置一些额外的参数。它是一个Map类型的参数，可以根据需要传递一些额外的参数。例如，可以设置队列的最大长度、超时时间等。
     */
//            channel.queueDeclare(queuedeadName,true,false,false,null);
//            channel.queueBind(queuedeadName,BI_EXCHANGE_DEAD,"");
//
//            // 创建缓存队列交换机等
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
    Queue ChartDeadQueue(){
        return QueueBuilder.durable(MqConstant.CHART_DEAD_QUEUE_NAME).build();
    }

    @Bean
    DirectExchange ChartDeadExchange(){
        return new DirectExchange(MqConstant.CHART_DEAD_EXCHANGE_NAME);
    }

    @Bean
    Binding ChartDeadBinding(Queue ChartDeadQueue, DirectExchange ChartDeadExchange) {
        return BindingBuilder.bind(ChartDeadQueue).to(ChartDeadExchange).with(MqConstant.CHART_DEAD_ROUTING_KEY);
    }

    /*
        声明缓存
     */
    @Bean
    Queue ChartQueue(){
        HashMap<String, Object> arg = new HashMap<>();
        arg.put("x-message-ttl",60000);

        //绑定死信交换机
        arg.put("x-dead-letter-exchange", MqConstant.CHART_DEAD_EXCHANGE_NAME);
        arg.put("x-dead-letter-routing-key", MqConstant.CHART_DEAD_ROUTING_KEY);

        return QueueBuilder.durable(MqConstant.CHART_QUEUE_NAME).withArguments(arg).build();
    }

    @Bean
    DirectExchange ChartExchange(){
        return new DirectExchange(MqConstant.CHART_EXCHANGE_NAME);
    }

    @Bean
    Binding ChartBinding(Queue ChartQueue, DirectExchange ChartExchange) {
        return BindingBuilder.bind(ChartQueue).to(ChartExchange).with(MqConstant.CHART_ROUTING_KEY);
    }

}
