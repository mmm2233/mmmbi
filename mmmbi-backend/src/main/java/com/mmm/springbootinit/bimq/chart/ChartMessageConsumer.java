package com.mmm.springbootinit.bimq.chart;

import cn.hutool.core.date.DateTime;
import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.constant.CommonConstant;
import com.mmm.springbootinit.constant.GenConstant;
import com.mmm.springbootinit.constant.MqConstant;
import com.mmm.springbootinit.exception.BusinessException;
import com.mmm.springbootinit.manager.AiManager;
import com.mmm.springbootinit.model.entity.Chart;
import com.mmm.springbootinit.model.enums.FutureStatus;
import com.mmm.springbootinit.service.ChartService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Header;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.bimq
 * @Project：mmmbi-backend
 * @name：MyMessageConsumer
 * @Date：2024/3/7 18:34
 * @Filename：MyMessageConsumer
 */
@Configuration
@Slf4j
public class ChartMessageConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;

    @RabbitListener(queues ={MqConstant.CHART_QUEUE_NAME} ,ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG)long deliveryTag) throws IOException {
        log.info("receiveMessage message{}",message);

        if (StringUtils.isBlank(message)){
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"消息为空");
        }
        long chartId =Long.parseLong(message);
        Chart chart = chartService.getById(chartId);

        if (chart == null) {
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"图表为空");
        }
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus(FutureStatus.RUNING.getValue());
        boolean r = chartService.updateById(updateChart);
        if (!r){
            channel.basicNack(deliveryTag,false,false);
            handleChartUpdateError(chart.getId(),"更改图表正在审核中失败");
            return;
        }

        String res = aiManager.doChat(GenConstant.MODE_CHART_ID, buildUserInput(chart));
        String[] splits = res.split("【【【【");
        if (splits.length < 3) {
            // todo guaya Retrying重试机制，当AI响应错误时进行重试,这使用消息队列重新入队，通过空消息存活时间控制重试次数
            log.warn("信息放入队列{}", DateTime.now());
            channel.basicNack(deliveryTag,false,true);
            handleChartUpdateError(chart.getId(),"AI响应错误");
            return;
        }
        // todo 控制ai生成数据格式,可以通过正则判断生成格式正确，不正确进行重试
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();
        Chart aiupdateChart = new Chart();
        aiupdateChart.setId(chart.getId());
        aiupdateChart.setGenChart(genChart);
        aiupdateChart.setGenResult(genResult);
        aiupdateChart.setStatus(FutureStatus.SUCCEED.getValue());
        boolean updateResult = chartService.updateById(aiupdateChart);
        if (!updateResult){
            channel.basicNack(deliveryTag,false,false);
            handleChartUpdateError(chart.getId(),"更新图表成功状态失败");
        }

        // 任务执行成功，确认消息
        channel.basicAck(deliveryTag,false);

    }


    /*
        构造用户输入
     */
    private String buildUserInput(Chart chart) {
        String goal =chart.getGoal();
        String chartType = chart.getChartType();
        String csvData =chart.getChartData();

        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)){
            userGoal = userGoal + ".请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        userInput.append(csvData).append("\n");

        return userInput.toString();
    }

    private void handleChartUpdateError(Long chartId,String execMessage) {
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus(FutureStatus.FAILED.getValue());
        updateChartResult.setExecMessage(execMessage);
        boolean updateResult = chartService.updateById(updateChartResult);
        if (updateResult) {
            log.error("更新图表失败状态失败" + chartId + "," +execMessage);
        }
    }
}
