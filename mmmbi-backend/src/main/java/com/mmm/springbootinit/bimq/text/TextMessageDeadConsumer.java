package com.mmm.springbootinit.bimq.text;

import com.alibaba.excel.util.StringUtils;
import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.constant.MqConstant;
import com.mmm.springbootinit.constant.GenConstant;
import com.mmm.springbootinit.exception.BusinessException;
import com.mmm.springbootinit.model.entity.TextTask;
import com.mmm.springbootinit.service.TextTaskService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 文本转换队列的死信队列
 * @Author：mmm
 * @Package：com.mmm.springbootinit.bimq.text
 * @Project：mmmbi-backend
 * @name：TextMessageDeadConsumer
 * @Date：2024/3/9 21:11
 * @Filename：TextMessageDeadConsumer
 */

@Component
@Slf4j
public class TextMessageDeadConsumer {

    @Resource
    private TextTaskService textTaskService;


    @SneakyThrows
    @RabbitListener(queues = {MqConstant.TEXT_DEAD_QUEUE_NAME},ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag){
        log.warn("接收到死信队列信息，receiveMessage={}",message);
        if (StringUtils.isBlank(message)){
            //消息为空，消息拒绝，不重复发送，不重新放入队列
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"消息为空");
        }
        long textTaskId = Long.parseLong(message);
        TextTask textTask = textTaskService.getById(textTaskId);
        if (textTask == null){
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"文本为空");
        }

        //修改表状态为执行中，执行成功修改为“已完成”；执行失败修改为“失败”
        TextTask updateTextTask = new TextTask();
        updateTextTask.setId(textTask.getId());
        updateTextTask.setStatus(GenConstant.FAILED);
        boolean updateResult = textTaskService.updateById(updateTextTask);
        //这里不对记录表状态修改，记录只能内部使用
        if (!updateResult){
            textTaskService.handleTextTaskUpdateError(updateTextTask.getId(),"更新图表执行状态失败");
            return;
        }
        //消息确认
        channel.basicAck(deliveryTag,false);
    }


}
