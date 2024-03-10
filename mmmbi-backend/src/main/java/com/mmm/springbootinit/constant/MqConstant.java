package com.mmm.springbootinit.constant;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.constant
 * @Project：mmmbi-backend
 * @name：MqConstant
 * @Date：2024/3/9 21:13
 * @Filename：MqConstant
 */
public interface MqConstant {
    //文本AI生成队列

    String TEXT_EXCHANGE_NAME = "text_exchange";

    String TEXT_QUEUE_NAME="text_queue";

    String TEXT_ROUTING_KEY="text_routingKey";
    //死信
    String TEXT_DEAD_EXCHANGE_NAME="text_dead_exchange";
    String TEXT_DEAD_QUEUE_NAME="text_dead_queue";

    String TEXT_DEAD_ROUTING_KEY="text_dead_routingKey";

    //订单支付队列
    String ORDERS_EXCHANGE_NAME = "orders_exchange";

    String ORDERS_QUEUE_NAME="orders_queue";

    String ORDERS_ROUTING_KEY="orders_routingKey";
    //死信
    String ORDERS_DEAD_EXCHANGE_NAME="orders_dead_exchange";
    String ORDERS_DEAD_QUEUE_NAME="orders_dead_queue";

    String ORDERS_DEAD_ROUTING_KEY="orders_dead_routingKey";

    //图表队列
    String CHART_EXCHANGE_NAME = "chart_exchange";

    String CHART_QUEUE_NAME = "chart_queue";

    String CHART_ROUTING_KEY = "chart_routingKey";

    String CHART_DEAD_EXCHANGE_NAME="chart_dead_exchange";

    String CHART_DEAD_QUEUE_NAME="chart_dead_queue";

    String CHART_DEAD_ROUTING_KEY="chart_dead_routingKey";


    //缓存队列
    String CACHE_EXCHANGE_NAME = "cache_exchange";

    String CACHE_QUEUE_NAME = "cache_queue";

    String CACHE_ROUTING_KEY = "cache_routingKey";

    String CACHE_DEAD_EXCHANGE_NAME="cache_dead_exchange";

    String CACHE_DEAD_QUEUE_NAME="cache_dead_queue";

    String CACHE_DEAD_ROUTING_KEY="cache_dead_routingKey";
}
