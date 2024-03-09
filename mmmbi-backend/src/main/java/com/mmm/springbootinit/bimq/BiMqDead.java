package com.mmm.springbootinit.bimq;

import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.exception.BusinessException;
import com.mmm.springbootinit.exception.ThrowUtils;
import com.mmm.springbootinit.model.entity.Chart;
import com.mmm.springbootinit.model.enums.FutureStatus;
import com.mmm.springbootinit.service.ChartService;
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
 * @Package：com.mmm.springbootinit.bimq
 * @Project：mmmbi-backend
 * @name：BiMqDead
 * @Date：2024/3/9 9:55
 * @Filename：BiMqDead
 */
@Component
@Slf4j
public class BiMqDead {

    @Resource
    ChartService chartService;

    // todo 处理死信队列中的消息，刷新缓存失败消息直接丢弃（怎么失败？）,生成图表消息队列，AI响应异常的重试其他直接修改失败
    @SneakyThrows
    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_DEAD}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag){
        log.warn("接收到死信队列信息，receiveMessage={}",message);
        if (StringUtils.isBlank(message) || message.equals("刷新缓存")){
            // 消息为空 和 刷新缓存 直接拒绝
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"消息为空");
        }


        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        if (chart == null){
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"图表为空");
        }

        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus(FutureStatus.FAILED.getValue());
        boolean updateResult = chartService.updateById(updateChart);
        ThrowUtils.throwIf(!updateResult,ErrorCode.SYSTEM_ERROR);
        channel.basicAck(deliveryTag,false);
    }
}
