package com.mmm.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.constant.CommonConstant;
import com.mmm.springbootinit.exception.BusinessException;
import com.mmm.springbootinit.exception.ThrowUtils;
import com.mmm.springbootinit.model.dto.chart.ChartQueryRequest;
import com.mmm.springbootinit.model.entity.*;
import com.mmm.springbootinit.model.vo.ChartVO;
import com.mmm.springbootinit.model.vo.UserVO;
import com.mmm.springbootinit.service.ChartService;
import com.mmm.springbootinit.mapper.ChartMapper;
import com.mmm.springbootinit.service.UserService;
import com.mmm.springbootinit.utils.RedisUtils;
import com.mmm.springbootinit.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{
    @Resource
    private UserService userService;

    @Resource
    private ChartService chartService;

    private final String CACHE_USER_PAGE = "cache_user_page";

    private final String CACHE_PAGE_ARG = "cache_page_arg";

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void validChart(Chart chart, boolean add) {
        if (chart == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String chartType = chart.getChartType();
        String goal = chart.getGoal();
        String chartData = chart.getChartData();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(chartData, goal, chartData), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(chartType) &&chartType.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(chartData) && chartData.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param chartQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }
        String chartType = chartQueryRequest.getChartType();
        String goal = chartQueryRequest.getGoal();
        String chartName = chartQueryRequest.getChartName();
        Long id = chartQueryRequest.getId();
        Long userId = chartQueryRequest.getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();
        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(chartType), "chartType", chartType);
        queryWrapper.eq(StringUtils.isNotBlank(goal), "goal", goal);
        queryWrapper.like(StringUtils.isNotBlank(chartName), "chartName", chartName);
        queryWrapper.eq(id!=null && id > 0, "id", id);
        queryWrapper.eq(userId!=null && userId > 0, "userId", userId);

        // 添加排序条件
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public ChartVO getChartVO(Chart chart, HttpServletRequest request) {
        ChartVO chartVO = ChartVO.objToVo(chart);
        long chartId = chart.getId();
        // 1. 关联查询用户信息
        Long userId = chart.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        chartVO.setUser(userVO);

        return chartVO;
    }

    @Override
    public Page<ChartVO> getChartVOPage(Page<Chart> chartPage, HttpServletRequest request) {
        List<Chart> chartList = chartPage.getRecords();
        Page<ChartVO> chartVOPage = new Page<>(chartPage.getCurrent(), chartPage.getSize(), chartPage.getTotal());
        if (CollUtil.isEmpty(chartList)) {
            return chartVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = chartList.stream().map(Chart::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        List<ChartVO> chartVOList = chartList.stream().map(chart -> {
            ChartVO chartVO = ChartVO.objToVo(chart);
            Long userId = chart.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            chartVO.setUser(userService.getUserVO(user));
            return chartVO;
        }).collect(Collectors.toList());
        chartVOPage.setRecords(chartVOList);
        return chartVOPage;
    }

    @Override
    public Page<Chart> getChartPageByRedis(ChartQueryRequest chartQueryRequest,HttpServletRequest httpServletRequest) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();

        User loginUser = userService.getLoginUser(httpServletRequest);
        // 每个用户id唯一
        String pageUser = CACHE_USER_PAGE + loginUser.getId();
        // 根据需要查询的当前页和大小组合
        String userPageArg = CACHE_PAGE_ARG + current+ ":" + size;
        Object chartPageInfo = redisTemplate.opsForHash().get(pageUser, userPageArg);
        if (chartPageInfo == null){
            Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                    chartService.getQueryWrapper(chartQueryRequest));
            redisTemplate.opsForHash().put(pageUser,userPageArg,chartPage);
            redisTemplate.expire(pageUser,360 , TimeUnit.MINUTES);
            return chartPage;
        }

        Object userChartPageInfo = redisTemplate.opsForHash().get(pageUser, userPageArg);
        Page<Chart> cacheResult = RedisUtils.convertToPage(userChartPageInfo,Chart.class);
        return cacheResult;
    }
}




