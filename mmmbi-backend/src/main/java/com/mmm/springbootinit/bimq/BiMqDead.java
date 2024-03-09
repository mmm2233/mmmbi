package com.mmm.springbootinit.bimq;

import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.constant.CreditConstant;
import com.mmm.springbootinit.exception.BusinessException;
import com.mmm.springbootinit.exception.ThrowUtils;
import com.mmm.springbootinit.model.entity.Chart;
import com.mmm.springbootinit.model.enums.FutureStatus;
import com.mmm.springbootinit.service.ChartService;
import com.mmm.springbootinit.service.CreditService;
import com.mmm.springbootinit.service.UserService;
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

    @Resource
    CreditService creditService;

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
        //todo 进入死信队列可能是数据为空，或者保存失败和ai生成失败，那么如果有数据就代表生成成功保存失败这里重新保存
        if(chart.getChartData() != null){
            log.info("处理死信队列消息成功,保存图表id:{}", chart.getId());
            chart.setStatus(FutureStatus.SUCCEED.getValue());
            channel.basicAck(deliveryTag,false);
        }
        // 否则图表生成失败返回积分给用户
        Long userId = chart.getUserId();
        log.info("处理死信队列补偿用户积分,失败用户id:{},图表id{}", userId,chart.getId());
        creditService.consumeCredits(userId, CreditConstant.CREDIT_TIME_1);

        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus(FutureStatus.FAILED.getValue());
        boolean updateResult = chartService.updateById(updateChart);
        ThrowUtils.throwIf(!updateResult,ErrorCode.SYSTEM_ERROR);
        channel.basicAck(deliveryTag,false);
    }
}
