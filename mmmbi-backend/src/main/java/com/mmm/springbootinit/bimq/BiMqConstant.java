package com.mmm.springbootinit.bimq;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.bimq
 * @Project：mmmbi-backend
 * @name：BiMqConstant
 * @Date：2024/3/7 19:11
 * @Filename：BiMqConstant
 */
public interface BiMqConstant {
    String BI_EXCHANGE_NAME = "bi_exchange";

    String BI_QUEUE_NAME = "bi_queue";

    String BI_ROUTING_KEY = "bi_routingKey";

    String BI_EXCHANGE_DEAD = "bi_exchange_dead";

    String BI_QUEUE_DEAD = "bi_queue_dead";

    String BI_EXCHANGE_CACHE = "bi_exchange_cache";

    String BI_QUEUE_CACHE ="bi_queue_cache";

    String BI_ROUTING_CACHE = "bi_queue_cache";

    String BI_ROUTING_DEAD ="bi_routing_dead";

    String ORDERS_DEAD_QUEUE_NAME ="orders_dead_queue_name";

    String ORDERS_DEAD_EXCHANGE_NAME ="orders_dead_exchange_name";

    String ORDERS_DEAD_ROUTING_KEY ="orders_dead_routing_key";

    String ORDERS_QUEUE_NAME ="orders_queue_name";

    String ORDERS_EXCHANGE_NAME ="orders_exchange_name";

    String ORDERS_ROUTING_KEY = "orders_routing_key";
}
