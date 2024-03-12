package com.mmm.springbootinit.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.mmm.springbootinit.manager.RedisLimiterManager;
import com.mmm.springbootinit.model.document.ChartGen;
import com.mmm.springbootinit.model.dto.chart.*;
import com.mmm.springbootinit.model.entity.Chart;
import com.mmm.springbootinit.model.entity.User;
import com.mmm.springbootinit.model.enums.ChartStatusEnum;
import com.mmm.springbootinit.model.enums.PointChangeEnum;
import com.mmm.springbootinit.model.vo.BiResponse;
import com.mmm.springbootinit.model.vo.ChartVO;
import com.mmm.springbootinit.service.ChartService;
import com.mmm.springbootinit.service.PointsService;
import com.mmm.springbootinit.service.UserService;
import com.mmm.springbootinit.utils.ExcelUtils;
import com.mmm.springbootinit.utils.ServerLoadInfo;
import com.mmm.springbootinit.utils.ServerMetricsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

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
    private RedisLimiterManager redisLimiterManager;

    @Resource
    PointsService pointService;

    /**
     * 分页获取列表
     *
     * @param chartQueryRequest
     * @return
     */
//    @PostMapping("/list/page")
//    //todo 这里分页数据没有必要走缓存进行查询，分页数据不适合缓存，这里可以使用es加大搜索
//    // 速度这里练习下redis
////    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
//    public BaseResponse<Page<Chart>> listChartByPage(@RequestBody ChartQueryRequest chartQueryRequest, HttpServletRequest httpServletRequest) {
//        if (chartQueryRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//
//        Page<Chart> chartPageByRedis = chartService.getChartPageByRedis(chartQueryRequest, httpServletRequest);
//
//        return ResultUtils.success(chartPageByRedis);
//    }

    @PostMapping("/list/chart/unsucceed")
    public BaseResponse<Page> getUnsucceedChart(@RequestBody ChartQueryRequest chartQueryRequest,HttpServletRequest httpServletRequest) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        chartQueryRequest.setUserId(loginUser.getId());
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<Chart> wrapper = chartService.getQueryWrapper(chartQueryRequest,httpServletRequest);
        wrapper.ne("status", ChartStatusEnum.SUCCEED.getStatus());
        Page<Chart> page = chartService.page(new Page<>(current, size),
                wrapper);
        return ResultUtils.success(page);

    }

    @PostMapping("/list/chart/all")
    public BaseResponse<com.baomidou.mybatisplus.extension.plugins.pagination.Page> getAllCharts(@RequestBody ChartQueryRequest chartQueryRequest,HttpServletRequest httpServletRequest) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        chartQueryRequest.setUserId(loginUser.getId());
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<Chart> wrapper = chartService.getQueryWrapper(chartQueryRequest,httpServletRequest);
        Page<Chart> page = chartService.page(new Page<>(current, size), wrapper);
        List<ChartVO> chartVOS = page.getRecords().stream().map(item -> {
            ChartVO chartVO = BeanUtil.copyProperties(item, ChartVO.class);
            return chartVO;
        }).collect(Collectors.toList());
        Page newPage = BeanUtil.copyProperties(page, Page.class);
        newPage.setRecords(chartVOS);
        return ResultUtils.success(newPage);
    }

    @GetMapping("/regen/chart")
    public BaseResponse<String> regenerateChart(@RequestParam("chartId") Long chartId,HttpServletRequest httpServletRequest) {
        // 取出数据
        Chart chartEntity = chartService.getById(chartId);
        ThrowUtils.throwIf(chartEntity.getChartData().length() > 1000, ErrorCode.SYSTEM_ERROR, "原始信息过长!");
        // 获取用户信息
        User user = userService.getLoginUser(httpServletRequest);

        redisLimiterManager.doRateLimit(RedisConstant.GEN_CHART_LIMIT_KEY + user.getId());
        chartEntity.setStatus(ChartStatusEnum.WAIT.getStatus());
        // 更新状态信息
        boolean updateById = chartService.updateById(chartEntity);
        ThrowUtils.throwIf(!updateById, ErrorCode.SYSTEM_ERROR, "重新生成图表失败");
        // 2. send to rabbitMQ
        long newChartId = chartEntity.getId();

        mqMessageProducer.sendMessage(MqConstant.CHART_EXCHANGE_NAME,MqConstant.CHART_ROUTING_KEY,String.valueOf(newChartId));
        return ResultUtils.success("操作成功");
    }

    /**
     * 智能图表(异步) : 消息队列
     *
     */
    @PostMapping("/gen/async/mq")
    public BaseResponse<BiResponse> genChartByAi(@RequestPart("file") MultipartFile multipartFile,
                                                 GenCharByAIRequest chartRequest,HttpServletRequest httpServletRequest) {
        // 1.save chat(Not Generated)
        // 取出数据
        String chartType = chartRequest.getChartType();
        String name = chartRequest.getChartName();
        String goal = chartRequest.getGoal();
        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空!");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长!");

        //校验文件
        ExcelUtils.checkExcelFile(multipartFile);
        // 获取用户信息
        User user = userService.getLoginUser(httpServletRequest);
        redisLimiterManager.doRateLimit(RedisConstant.GEN_CHART_LIMIT_KEY + user.getId());
        // 读取文件信息
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        ThrowUtils.throwIf(csvData.length() > 1000, ErrorCode.SYSTEM_ERROR, "原始信息过长!");
        // 插入数据到数据库
        Chart chartEntity = new Chart();
        chartEntity.setUserId(user.getId());
        chartEntity.setChartName(name);
        chartEntity.setGoal(goal);
        chartEntity.setStatus(ChartStatusEnum.WAIT.getStatus());
        chartEntity.setChartType(chartType);
        chartEntity.setChartData(csvData);
        boolean save = chartService.save(chartEntity);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR, "保存图表失败!");
        // 在这里选择执行的策略
        //1. 获取当前执行状态
        ServerLoadInfo info = ServerMetricsUtil.getLoadInfo();
        //2. 执行图表生成
        // 扣除对应的积分
        boolean res = pointService.checkAndDeduct(user.getId(), PointChangeEnum.GEN_CHART_DEDUCT);
        if (!res){
            return ResultUtils.error(ErrorCode.POINTS_NE);
        }
        try{
            BiResponse biResponse = chartService.genChart(chartEntity,info);
            if (StringUtils.isNotBlank(biResponse.getGenChart())) {
                return ResultUtils.success(biResponse);
            }
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR);
        }catch (BusinessException businessException){
            if(businessException.getCode() == ErrorCode.TOO_MANY_REQUEST.getCode()){
                // 如果执行的是拒绝策略 , 那么退回扣除的积分
                pointService.sendCompensateMessage(user.getId(),PointChangeEnum.GEN_CHART_FAILED_COMPENSATE);
            }
            throw businessException;
        }
    }

    /**
     * 创建
     *
     * @param chartAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChartEntity(@RequestBody ChartAddRequest chartAddRequest,HttpServletRequest httpServletRequest) {
        if (chartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);
        User loginUser = userService.getLoginUser(httpServletRequest);
        chart.setUserId(loginUser.getId());
        boolean result = chartService.save(chart);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newChartEntityId = chart.getId();
        return ResultUtils.success(newChartEntityId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete/doc")
    public BaseResponse<Boolean> deleteChartDocument(@RequestBody DeleteChartDocRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Chart oldChartEntity = chartService.getById(id);
        ThrowUtils.throwIf(oldChartEntity == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChartEntity.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = chartService.deleteSingleFromMongo(id,deleteRequest.getVersion());
        // 如果文档都被删除完那么就修改图表的状态WAIT
        if (chartService.getChartByChartId(oldChartEntity.getId()) == null) {
            oldChartEntity.setStatus(ChartStatusEnum.WAIT.getStatus());
            oldChartEntity.setExecMessage(ChartStatusEnum.WAIT.getMessage());
            chartService.updateById(oldChartEntity);
        }
        return ResultUtils.success(result);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete/do")
    public BaseResponse<Boolean> deleteChartEntity(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Chart oldChartEntity = chartService.getById(id);
        ThrowUtils.throwIf(oldChartEntity == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChartEntity.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = chartService.deleteAllFromMongo(id);
        boolean b = chartService.removeById(id);
        return ResultUtils.success(b && result);
    }

    /**
     * 更新（仅管理员）
     *
     * @param chartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateChartEntity(@RequestBody ChartUpdateRequest chartUpdateRequest) {
        if (chartUpdateRequest == null || chartUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartUpdateRequest, chart);
        long id = chartUpdateRequest.getId();
        // 判断是否存在
        Chart oldChartEntity = chartService.getById(id);
        ThrowUtils.throwIf(oldChartEntity == null, ErrorCode.NOT_FOUND_ERROR);
        // 查看用户是否修改了原始数据 : 重新提交到AI服务进行图表生成
        if (!oldChartEntity.getChartData().equals(chartUpdateRequest.getChartData())) {
            // 发送消息到AI生成模块重新进行生成
            mqMessageProducer.sendMessage(MqConstant.CHART_EXCHANGE_NAME,MqConstant.CHART_ROUTING_KEY,String.valueOf(oldChartEntity.getId()));
        }
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<ChartGen> getChartEntityById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ChartGen chart = chartService.getChartByChartId(id);
        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(chart);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param chartQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<org.springframework.data.domain.Page<ChartGen>> listChartEntityByPage(@RequestBody ChartQueryRequest chartQueryRequest,HttpServletRequest request) {
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        org.springframework.data.domain.Page<ChartGen> charts = chartService.getChartList(chartQueryRequest,request);
        return ResultUtils.success(charts);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param chartQueryRequest
     * @return
     */
    @PostMapping("/my/list/page")
    public BaseResponse<org.springframework.data.domain.Page<ChartGen>> listMyChartEntityByPage(@RequestBody ChartQueryRequest chartQueryRequest,HttpServletRequest request) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        chartQueryRequest.setUserId(loginUser.getId());
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        org.springframework.data.domain.Page<ChartGen> charts = chartService.getChartList(chartQueryRequest,request);
        return ResultUtils.success(charts);
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param chartEditRequest
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editChartEntity(@RequestBody ChartEditRequest chartEditRequest,HttpServletRequest request) {
        if (chartEditRequest == null || chartEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartEditRequest, chart);

        User loginUser = userService.getLoginUser(request);
        long id = chartEditRequest.getId();
        // 判断是否存在
        Chart oldChartEntity = chartService.getById(id);
        ThrowUtils.throwIf(oldChartEntity == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldChartEntity.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = chartService.updateById(chart);
        // 查看用户是否修改了原始数据 : 重新提交到AI服务进行图表生成
        if (!oldChartEntity.getChartData().equals(chartEditRequest.getChartData())) {
            // 发送消息到AI生成模块重新进行生成
            mqMessageProducer.sendMessage(MqConstant.CHART_EXCHANGE_NAME,MqConstant.CHART_ROUTING_KEY,String.valueOf(oldChartEntity.getId()));
        }
        return ResultUtils.success(result);
    }
}
