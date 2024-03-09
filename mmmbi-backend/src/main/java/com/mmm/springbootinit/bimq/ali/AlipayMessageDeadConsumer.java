package com.mmm.springbootinit.bimq.ali;

import com.alibaba.excel.util.StringUtils;
import com.mmm.springbootinit.bimq.BiMqConstant;
import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.constant.OrdersConstant;
import com.mmm.springbootinit.exception.BusinessException;
import com.mmm.springbootinit.service.OrdersinfoService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.bimq.ali
 * @Project：mmmbi-backend
 * @name：AlipayMessageDeadConsumer
 * @Date：2024/3/9 13:13
 * @Filename：AlipayMessageDeadConsumer
 */
@Component
@Slf4j
public class AlipayMessageDeadConsumer {

//    @Resource
//    private OrdersinfoService ordersService;
//
//    @SneakyThrows
//    @RabbitListener(queues = {BiMqConstant.ORDERS_DEAD_QUEUE_NAME})
//    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
//        log.warn("接收到死信队列信息，receiveMessage={}",message);
//        if (StringUtils.isBlank(message)){
//            //消息为空，消息拒绝，不重复发送，不重新放入队列
//            channel.basicNack(deliveryTag,false,false);
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"消息为空");
//        }
//        long ordersId = Long.parseLong(message);
//        Orders orders = ordersService.getById(ordersId);
//        if (orders == null){
//            channel.basicNack(deliveryTag,false,false);
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"订单为空");
//        }
//        Orders updateOrders = new Orders();
//        updateOrders.setId(orders.getId());
//        updateOrders.setTradeStatus(OrdersConstant.FAILED);
//        boolean updateResult = ordersService.updateById(updateOrders);
//        if (!updateResult){
//            handleOrdersUpdateError(orders.getId(),"更新订单执行状态失败");
//            return;
//        }
//        channel.basicAck(deliveryTag,false);
//    }
//
//    private void handleOrdersUpdateError(Long ordersId, String execMessage) {
//        Orders updateOrdersResult = new Orders();
//        updateOrdersResult.setTradeStatus(OrdersConstant.FAILED);
//        updateOrdersResult.setId(ordersId);
//        boolean updateResult = ordersService.updateById(updateOrdersResult);
//        //ThrowUtils.throwIf(!updateResult,ErrorCode.SYSTEM_ERROR);
//        if (!updateResult){
//            log.error("更新订单失败状态失败"+ordersId+","+execMessage);
//        }
//    }
}
