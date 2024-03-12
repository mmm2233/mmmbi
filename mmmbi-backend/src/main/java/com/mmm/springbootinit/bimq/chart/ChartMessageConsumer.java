package com.mmm.springbootinit.bimq.chart;

import cn.hutool.core.date.DateTime;
import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.constant.GenConstant;
import com.mmm.springbootinit.constant.MqConstant;
import com.mmm.springbootinit.exception.BusinessException;
import com.mmm.springbootinit.exception.ThrowUtils;
import com.mmm.springbootinit.manager.AiManager;
import com.mmm.springbootinit.model.entity.Chart;
import com.mmm.springbootinit.model.enums.ChartStatusEnum;
import com.mmm.springbootinit.service.ChartLogsService;
import com.mmm.springbootinit.service.ChartService;
import com.mmm.springbootinit.utils.ChartUtil;
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
    private ChartLogsService logsService;

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
        updateChart.setStatus(ChartStatusEnum.RUNNING.getStatus());
        boolean r = chartService.updateById(updateChart);
        if (!r){
            channel.basicNack(deliveryTag,false,false);
            handleChartUpdateError(chart.getId(),"更改图表正在审核中失败");
            return;
        }

        String res = aiManager.doChat(GenConstant.MODE_CHART_ID, ChartUtil.buildUserInput(chart));
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
        String compressedChart = ChartUtil.compressJson(genChart);
        String genResult = splits[2].trim();

        chart.setStatus(ChartStatusEnum.SUCCEED.getStatus());
        chart.setExecMessage(ChartStatusEnum.SUCCEED.getMessage());
        // 保存数据到MongoDB
        boolean syncResult = chartService.syncChart(chart, compressedChart, genResult);
        boolean updateGenResult = chartService.updateById(chart);
        ThrowUtils.throwIf(!(updateGenResult && syncResult), ErrorCode.SYSTEM_ERROR, "生成图表保存失败!");
        logsService.recordLog(chart);

        // 任务执行成功，确认消息
        channel.basicAck(deliveryTag,false);

    }

    private void handleChartUpdateError(Long chartId,String execMessage) {
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus(ChartStatusEnum.FAILED.getStatus());
        updateChartResult.setExecMessage(execMessage);
        boolean updateResult = chartService.updateById(updateChartResult);
        if (updateResult) {
            log.error("更新图表失败状态失败" + chartId + "," +execMessage);
        }
    }
}
