package com.mmm.springbootinit.service.execution;

import com.mmm.springbootinit.bimq.common.MqMessageProducer;
import com.mmm.springbootinit.constant.MqConstant;
import com.mmm.springbootinit.model.entity.Chart;
import com.mmm.springbootinit.model.vo.BiResponse;
import com.mmm.springbootinit.service.GenChartStrategy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.service.execution
 * @Project：mmmbi-backend
 * @name：GenChartMQ
 * @Date：2024/3/12 13:17
 * @Filename：GenChartMQ
 */
@Component(value = "gen_mq")
public class GenChartMQ implements GenChartStrategy {

    @Resource
    MqMessageProducer biMqMessageProducer;

    @Override
    public BiResponse executeGenChart(Chart chart) {
        long newChartId = chart.getId();
        biMqMessageProducer.sendMessage(MqConstant.CHART_EXCHANGE_NAME,MqConstant.CHART_ROUTING_KEY,String.valueOf(newChartId));
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(newChartId);
        return biResponse;
    }
}
