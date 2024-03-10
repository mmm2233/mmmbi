package com.mmm.springbootinit.bimq.cache;

import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.constant.MqConstant;
import com.mmm.springbootinit.exception.BusinessException;
import com.mmm.springbootinit.model.entity.Chart;
import com.mmm.springbootinit.service.ChartService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.bimq
 * @Project：mmmbi-backend
 * @name：BiMqCache
 * @Date：2024/3/8 22:26
 * @Filename：BiMqCache
 */
@Slf4j
@Component
public class CacheMessageConsumer {

    @Resource
    private RedisTemplate redisTemplate;

    // 接受消息，更新缓存
    @SneakyThrows
    @RabbitListener(queues = {MqConstant.CACHE_QUEUE_NAME},ackMode = "MANUAL")
    public void recacheMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag){
        log.info("bi start receiveMessage message = {}", message);

        if (StringUtils.isBlank(message)){
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
        }

        Set keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);

    }

}
