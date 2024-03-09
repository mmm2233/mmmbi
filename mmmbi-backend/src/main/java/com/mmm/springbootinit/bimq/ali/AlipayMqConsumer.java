package com.mmm.springbootinit.bimq.ali;

import com.mmm.springbootinit.bimq.BiMqConstant;
import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.constant.OrdersConstant;
import com.mmm.springbootinit.exception.BusinessException;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.bimq.ali
 * @Project：mmmbi-backend
 * @name：AlipayMqConsumer
 * @Date：2024/3/9 12:48
 * @Filename：AlipayMqConsumer
 */
@Component
@Slf4j
public class AlipayMqConsumer {

//    @Resource
//    OrdersService ordersService;
//
//    @SneakyThrows
//    @RabbitListener(queues = {BiMqConstant.ORDERS_DEAD_QUEUE_NAME})
//    public void receiveMessage(String message, Channel channel , @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag){
//        log.warn("接收到队列信息，receiveMessage={}",message);
//        if (StringUtils.isBlank(message)) {
//            // 消息为空，消息拒绝
//            channel.basicNack(deliveryTag,false,false);
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"消息为空");
//        }
//        long orderId = Long.parseLong(message);
//        Orders order = ordersService.getById(orderId);
//        if (order == null) {
//            channel.basicNack(deliveryTag,false,false);
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"订单为空");
//        }
//        //查看订单是否完成支付，未支付则重新放入队列直到过期
//        String tradeStatus = order.getTradeStatus();
//        log.warn("订单查询为"+order.getTradeStatus());
//        if (!tradeStatus.equals(OrdersConstant.SUCCEED)){
//            log.warn("订单未支付成功,重新放回队列,订单号为"+order.getId());
//            channel.basicNack(deliveryTag,false,true);
//        }else {
//            //消息确认
//            channel.basicAck(deliveryTag,false);
//        }
//    }
}
