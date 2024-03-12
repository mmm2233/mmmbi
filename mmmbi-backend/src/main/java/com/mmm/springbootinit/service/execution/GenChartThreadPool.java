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
import org.apache.commons.lang3.StringUtils;
import org.java_websocket.server.WebSocketServer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.service.execution
 * @Project：mmmbi-backend
 * @name：GenChartThreadPool
 * @Date：2024/3/12 12:35
 * @Filename：GenChartThreadPool
 */
@Slf4j
@Component(value = "gen_thread_pool")
public class GenChartThreadPool implements GenChartStrategy {
    
    @Resource
    ChartService chartService;

    @Resource
    AiManager aiManager;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    ChartLogsService logService;

    @Resource
    PointsService pointService;

    @Override
    public BiResponse executeGenChart(Chart chartEntity) {
        try {
            CompletableFuture.runAsync(() -> {
                Chart genChart = new Chart();
                String goal = chartEntity.getGoal();
                String chartType = chartEntity.getChartType();
                String csvData = chartEntity.getChartData();

                genChart.setId(chartEntity.getId());
                genChart.setStatus(ChartStatusEnum.RUNNING.getStatus());
                boolean b = chartService.updateById(genChart);
                // 处理异常
                ThrowUtils.throwIf(!b, new BusinessException(ErrorCode.SYSTEM_ERROR, "修改图表状态信息失败 " + chartEntity.getId()));
                // 获取CSV
                // 构造用户输入
                StringBuilder userInput = new StringBuilder("");
                // 拼接图表类型;
                String userGoal = goal;
                if (StringUtils.isNotBlank(chartType)) {
                    userGoal += ", 请使用 " + chartType;
                }
                userInput.append("分析需求: ").append('\n');
                userInput.append(userGoal).append("\n");
                userInput.append("原始数据：").append("\n");
                userInput.append(csvData).append("\n");
                // 系统预设 ( 简单预设 )
                /* 较好的做法是在系统（模型）层面做预设效果一般来说，会比直接拼接在用户消息里效果更好一些。*/
                String result = aiManager.doChat(AiConstant.MODE_CHART_ID,userInput.toString());
                String[] split = result.split("【【【【");
                // 第一个是 空字符串
                if (split.length < 3) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误!");
                }
                // 图表代码
                String genChartStr = split[1].trim();
                // 分析结果
                String genResult = split[2].trim();
                String compressJson = ChartUtil.compressJson(genChartStr);
                // 更新数据
                chartEntity.setStatus(ChartStatusEnum.SUCCEED.getStatus());
                chartEntity.setExecMessage("生成成功");
                boolean updateGenResult = chartService.updateById(chartEntity);

                boolean syncResult = chartService.syncChart(chartEntity, compressJson, genResult);
                ThrowUtils.throwIf(!updateGenResult && syncResult, ErrorCode.SYSTEM_ERROR, "生成图表保存失败!");
                // 记录调用结果
                logService.recordLog(chartEntity);
//                try {
//                    //todo websocket提醒用户查看生成图表
////                    webSocketServer.sendMessage("您的[" + chartEntity.getName() + "]生成成功 , 前往 我的图表 进行查看",
////                            new HashSet<>(Arrays.asList(chartEntity.getUserId().toString())));
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
            }, threadPoolExecutor);
        } catch (BusinessException e) {
            // 更新状态信息
            Chart updateChartResult = new Chart();
            updateChartResult.setId(chartEntity.getId());
            updateChartResult.setStatus(ChartStatusEnum.FAILED.getStatus());
            updateChartResult.setExecMessage(e.getMessage());
            boolean updateResult = chartService.updateById(updateChartResult);
            // 补偿积分
            pointService.sendCompensateMessage(chartEntity.getUserId(), PointChangeEnum.GEN_CHART_FAILED_ADD);
            // 记录调用结果: 这里的recordLog不会与上面的冲突,如果上面的执行了那么图表的生成结果一定是成功,不会执行到这里
            logService.recordLog(chartEntity);
            if (!updateResult) {
                log.info("更新图表FAILED状态信息失败 , chatId:{}", updateChartResult.getId());
            }
            // 抛出异常进行日志打印
            throw e;
        }
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chartEntity.getId());
        return biResponse;
    }
}
