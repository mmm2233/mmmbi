package com.mmm.springbootinit.service.execution;

import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.constant.AiConstant;
import com.mmm.springbootinit.exception.BusinessException;
import com.mmm.springbootinit.exception.ThrowUtils;
import com.mmm.springbootinit.manager.AiManager;
import com.mmm.springbootinit.model.entity.Chart;
import com.mmm.springbootinit.model.enums.ChartStatusEnum;
import com.mmm.springbootinit.model.enums.PointChangeEnum;
import com.mmm.springbootinit.model.vo.BiResponse;
import com.mmm.springbootinit.service.ChartLogsService;
import com.mmm.springbootinit.service.ChartService;
import com.mmm.springbootinit.service.GenChartStrategy;
import com.mmm.springbootinit.service.PointsService;
import com.mmm.springbootinit.utils.ChartUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.service.execution
 * @Project：mmmbi-backend
 * @name：GenChartSync
 * @Date：2024/3/12 12:34
 * @Filename：GenChartSync
 */
@Component(value = "gen_sync")
@Slf4j
public class GenChartSync implements GenChartStrategy {

    @Resource
    ChartService chartService;

    @Resource
    AiManager aiManager;

    @Resource
    ChartLogsService logService;

    @Resource
    PointsService pointService;

    @Override
    public BiResponse executeGenChart(Chart chart) {
        try{
            String userInput = ChartUtil.buildUserInput(chart);
            String result = aiManager.doChat(AiConstant.MODE_CHART_ID,userInput);
            String[] split = result.split("【【【【");
            // 第一个是 空字符串
            if (split.length < 3) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误!");
            }
            // 图表代码
            String genChart = split[1].trim();
            // 分析结果
            String genResult = split[2].trim();
            // 更新数据到数据库
            chart.setStatus(ChartStatusEnum.SUCCEED.getStatus());
            chart.setExecMessage("生成成功");
            genChart = ChartUtil.compressJson(genChart);
            boolean save = chartService.updateById(chart);
            ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR, "图表保存失败!");
            boolean syncResult = chartService.syncChart(chart,genChart,genResult);
            ThrowUtils.throwIf(!syncResult, ErrorCode.SYSTEM_ERROR, "图表同步失败!");
            // 记录生成日志
            logService.recordLog(chart);
            // 封装返回结果
            BiResponse biResponse = new BiResponse();
            biResponse.setGenChart(genChart);
            biResponse.setChartId(chart.getId());
            biResponse.setGenResult(genResult);
            return biResponse;
        } catch (BusinessException e) {
            // 更新状态信息
            Chart updateChartResult = new Chart();
            updateChartResult.setId(chart.getId());
            updateChartResult.setStatus(ChartStatusEnum.FAILED.getStatus());
            updateChartResult.setExecMessage(e.getMessage());
            boolean updateResult = chartService.updateById(updateChartResult);
            // 记录生成日志
            logService.recordLog(chart);
            if (!updateResult) {
                log.info("更新图表FAILED状态信息失败 , chatId:{}", updateChartResult.getId());
            }
            pointService.sendCompensateMessage(chart.getUserId(), PointChangeEnum.GEN_CHART_FAILED_ADD);
            // 抛出异常进行日志打印
            throw e;
        }
    }
}
