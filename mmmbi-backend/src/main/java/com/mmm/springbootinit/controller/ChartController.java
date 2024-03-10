package com.mmm.springbootinit.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmm.springbootinit.annotation.AuthCheck;
import com.mmm.springbootinit.bimq.common.MqMessageProducer;
import com.mmm.springbootinit.common.BaseResponse;
import com.mmm.springbootinit.common.DeleteRequest;
import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.common.ResultUtils;
import com.mmm.springbootinit.constant.*;
import com.mmm.springbootinit.exception.BusinessException;
import com.mmm.springbootinit.exception.ThrowUtils;
import com.mmm.springbootinit.manager.AiManager;
import com.mmm.springbootinit.manager.RedisLimiterManager;
import com.mmm.springbootinit.model.dto.chart.*;
import com.mmm.springbootinit.model.entity.Chart;
import com.mmm.springbootinit.model.entity.User;
import com.mmm.springbootinit.model.enums.FutureStatus;
import com.mmm.springbootinit.model.vo.BiResponse;
import com.mmm.springbootinit.model.vo.ChartVO;
import com.mmm.springbootinit.service.ChartService;
import com.mmm.springbootinit.service.CreditService;
import com.mmm.springbootinit.service.UserService;
import com.mmm.springbootinit.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
*@Date：2024/3/2
*@Author：mmm
*@return：
* 帖子接口
*/
@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    //todo 提出来
    private static final Long modeId = GenConstant.MODE_CHART_ID;

    @Resource
    private MqMessageProducer mqMessageProducer;

    @Resource
    private ChartService chartService;

    @Resource
    private UserService userService;

    @Resource
    private AiManager aiManager;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private CreditService creditService;

    // region 增删改查

    /**
     * 创建
     *
     * @param chartAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChart(@RequestBody ChartAddRequest chartAddRequest, HttpServletRequest request) {
        if (chartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);
        chartService.validChart(chart, true);
        User loginUser = userService.getLoginUser(request);
        chart.setUserId(loginUser.getId());
        boolean result = chartService.save(chart);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        if (result){
            mqMessageProducer.sendMessage(MqConstant.CACHE_EXCHANGE_NAME, MqConstant.CACHE_ROUTING_KEY,"刷新缓存");
        }
        long newChartId = chart.getId();
        return ResultUtils.success(newChartId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChart.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = chartService.removeById(id);
        if (b){
            mqMessageProducer.sendMessage(MqConstant.CACHE_EXCHANGE_NAME, MqConstant.CACHE_ROUTING_KEY,"刷新缓存");
        }
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param chartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateChart(@RequestBody ChartUpdateRequest chartUpdateRequest) {
        if (chartUpdateRequest == null || chartUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartUpdateRequest, chart);
        // 参数校验
        chartService.validChart(chart, false);
        long id = chartUpdateRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = chartService.updateById(chart);
        if (result) {
            mqMessageProducer.sendMessage(MqConstant.CACHE_EXCHANGE_NAME, MqConstant.CACHE_ROUTING_KEY,"刷新缓存");
        }
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<ChartVO> getChartVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = chartService.getById(id);
        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(chartService.getChartVO(chart, request));
    }

    /**
     * 分页获取列表
     *
     * @param chartQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    //todo 这里分页数据没有必要走缓存进行查询，分页数据不适合缓存，这里可以使用es加大搜索
    // 速度这里练习下redis
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Chart>> listChartByPage(@RequestBody ChartQueryRequest chartQueryRequest, HttpServletRequest httpServletRequest) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Page<Chart> chartPageByRedis = chartService.getChartPageByRedis(chartQueryRequest, httpServletRequest);

        return ResultUtils.success(chartPageByRedis);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ChartVO>> listChartVOByPage(@RequestBody ChartQueryRequest chartQueryRequest,
                                                         HttpServletRequest request) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                chartService.getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartService.getChartVOPage(chartPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<ChartVO>> listMyChartVOByPage(@RequestBody ChartQueryRequest chartQueryRequest,
                                                           HttpServletRequest request) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        chartQueryRequest.setUserId(loginUser.getId());
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                chartService.getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartService.getChartVOPage(chartPage, request));
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param chartEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editChart(@RequestBody ChartEditRequest chartEditRequest, HttpServletRequest request) {
        if (chartEditRequest == null || chartEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartEditRequest, chart);
        // 参数校验
        chartService.validChart(chart, false);
        User loginUser = userService.getLoginUser(request);
        long id = chartEditRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldChart.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = chartService.updateById(chart);
        if (result) {
            mqMessageProducer.sendMessage(MqConstant.CACHE_EXCHANGE_NAME, MqConstant.CACHE_ROUTING_KEY,"刷新缓存");
        }
        return ResultUtils.success(result);
    }

    /**
     * 智能分析（同步）
     *
     * @param multipartFile
     * @param request
     * @return
     */
    @PostMapping("/gen")
    public BaseResponse<BiResponse> getChartByAI(@RequestPart("file") MultipartFile multipartFile,
                                                 GenCharByAIRequest genCharByAIRequest, HttpServletRequest request) {
        Chart chart = getChart(multipartFile, genCharByAIRequest, request);

        Boolean creditResult = creditService.consumeCredits(chart.getUserId(),CreditConstant.CREDIT_TIME);
        ThrowUtils.throwIf(!creditResult,ErrorCode.OPERATION_ERROR,"你的积分不足");
        // todo 扣除积分之后，立即保存不知道有没有问题
        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败！");

        String res = aiManager.doChat(modeId, chart.getGenResult());
        String[] splits = res.split("【【【【");
        if (splits.length < 3) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI响应错误");
        }
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();

        // to do同步直接修改
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setStatus(FutureStatus.SUCCEED.getValue());

        saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败！");
        if(saveResult){
            mqMessageProducer.sendMessage(MqConstant.CACHE_EXCHANGE_NAME, MqConstant.CACHE_ROUTING_KEY,"刷新缓存");
        }
        BiResponse biResponse = new BiResponse();
        biResponse.setGenChart(genChart);
        biResponse.setGenResult(genResult);
        biResponse.setChartId(chart.getId());

        return ResultUtils.success(biResponse);
    }

    /**
     * 智能分析
     *
     * @param multipartFile
     * @param request
     * @return
     */
    @PostMapping("/gen/async")
    public BaseResponse<BiResponse> getChartByAIAsync(@RequestPart("file") MultipartFile multipartFile,
                                                      GenCharByAIRequest genCharByAIRequest, HttpServletRequest request) {
        Chart chart = getChart(multipartFile, genCharByAIRequest, request);
        Boolean creditResult = creditService.consumeCredits(chart.getUserId(),CreditConstant.CREDIT_TIME);
        ThrowUtils.throwIf(!creditResult,ErrorCode.OPERATION_ERROR,"你的积分不足");
        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败！");
        // todo 处理任务状态满了爆异常，处理任务设置超时时间
        // todo 任务执行成功给用户发送消息（实时websocket，server side event）
        try {
            CompletableFuture.runAsync(() -> {
                // 先将任务标记为正在执行中，方便处理任务重复执行，和前端更容易知道任务状态
                Chart updateChart = new Chart();
                updateChart.setId(chart.getId());
                updateChart.setStatus(FutureStatus.RUNING.getValue());
                boolean r = chartService.updateById(updateChart);
                if (!r) {
                    handleChartUpdateError(chart.getId(), "更改图表正在审核中失败");
                    return;
                }

                String res = aiManager.doChat(modeId, chart.getGenResult());
                String[] splits = res.split("【【【【");
                if (splits.length < 3) {
                    // todo guaya Retrying重试机制，当AI响应错误时进行重试
                    handleChartUpdateError(chart.getId(), "AI响应错误");
                    return;
                }
                // todo 控制ai生成数据格式
                String genChart = splits[1].trim();
                String genResult = splits[2].trim();
                Chart aiupdateChart = new Chart();
                aiupdateChart.setId(chart.getId());
                aiupdateChart.setGenChart(genChart);
                aiupdateChart.setGenResult(genResult);
                aiupdateChart.setStatus(FutureStatus.SUCCEED.getValue());
                boolean updateResult = chartService.updateById(aiupdateChart);
                if (!updateResult) {
                    handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
                }else {
                    mqMessageProducer.sendMessage(MqConstant.CACHE_EXCHANGE_NAME, MqConstant.CACHE_ROUTING_KEY,"刷新缓存");
                }
            }, threadPoolExecutor);
        } catch (RejectedExecutionException e) {
            //todo 任务队列满了，使用定时任务将失败状态图表放到队列中
            handleChartUpdateError(chart.getId(), "生成图表执行任务失败");
        }

        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chart.getId());

        return ResultUtils.success(biResponse);
    }

    // 异步消息队列
    @PostMapping("/gen/async/mq")
    public BaseResponse<BiResponse> getChartByAIAsyncMq(@RequestPart("file") MultipartFile multipartFile,
                                                        GenCharByAIRequest genCharByAIRequest, HttpServletRequest request) {
        Chart chart = getChart(multipartFile, genCharByAIRequest, request);
        Boolean creditResult = creditService.consumeCredits(chart.getUserId(),CreditConstant.CREDIT_TIME);
        ThrowUtils.throwIf(!creditResult,ErrorCode.OPERATION_ERROR,"你的积分不足");
        boolean saveResult = chartService.save(chart);

        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败！");
        if (saveResult) {
            mqMessageProducer.sendMessage(MqConstant.CACHE_EXCHANGE_NAME, MqConstant.CACHE_ROUTING_KEY,"刷新缓存");
        }
        long newChartId = chart.getId();

        mqMessageProducer.sendMessage(MqConstant.CHART_EXCHANGE_NAME,MqConstant.CHART_ROUTING_KEY,String.valueOf(newChartId));

        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chart.getId());

        return ResultUtils.success(biResponse);
    }

    private Chart getChart(MultipartFile multipartFile, GenCharByAIRequest genCharByAIRequest, HttpServletRequest request) {
        String goal = genCharByAIRequest.getGoal();
        String chartName = genCharByAIRequest.getChartName();
        String chartType = genCharByAIRequest.getChartType();

        //校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(chartName) && chartName.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");
        //校验文件
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        final long ONE_MB = 1024 * 1024L;
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件大小超过1M");
        // 校验文件后缀
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffixList = Arrays.asList("xls", "xlsx");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");

        User loginUser = userService.getLoginUser(request);

        // 限流判断
        redisLimiterManager.doRateLimit("getChartByAI_" + loginUser.getId());

        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal = userGoal + ".请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        // 压缩数据
        String chartData = ExcelUtils.excelToCsv(multipartFile);
        userInput.append(chartData).append("\n");

        // to db
        Chart chart = new Chart();
        chart.setGoal(userGoal);
        chart.setChartName(chartName);
        chart.setChartData(chartData);
        chart.setChartType(chartType);
        // 规范来说 可以将线程池和这部分代码提出来到一个方法中，懒得写了，保存在 genresult结论中，之后在用ai生成的覆盖掉。
        chart.setGenResult(userInput.toString());
        chart.setStatus(FutureStatus.WAIT.getValue());
        chart.setUserId(loginUser.getId());
        return chart;
    }

    private void handleChartUpdateError(Long chartId, String execMessage) {
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus(FutureStatus.FAILED.getValue());
        updateChartResult.setExecMessage(execMessage);
        boolean updateResult = chartService.updateById(updateChartResult);
        if (updateResult) {
            log.error("更新图表失败状态失败" + chartId + "," + execMessage);
        }
    }

}
