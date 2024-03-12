package com.mmm.springbootinit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmm.springbootinit.constant.CommonConstant;
import com.mmm.springbootinit.manager.StrategySelector;
import com.mmm.springbootinit.model.document.ChartGen;
import com.mmm.springbootinit.model.dto.chart.ChartQueryRequest;
import com.mmm.springbootinit.model.entity.*;
import com.mmm.springbootinit.model.vo.BiResponse;
import com.mmm.springbootinit.repository.ChartRepository;
import com.mmm.springbootinit.service.ChartService;
import com.mmm.springbootinit.mapper.ChartMapper;
import com.mmm.springbootinit.service.GenChartStrategy;
import com.mmm.springbootinit.service.UserService;
import com.mmm.springbootinit.utils.ServerLoadInfo;
import com.mmm.springbootinit.utils.SqlUtils;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 *
 */
@Service
@Slf4j
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{

    @Resource
    ChartRepository chartRepository;

    @Resource
    MongoTemplate mongoTemplate;

    @Resource
    UserService userService;

    @Resource
    @Lazy
    StrategySelector strategySelector;

    @Override
    public boolean saveDocument(ChartGen chart) {
        Long chartId = chart.getChartId();
        List<ChartGen> charts = chartRepository.findAllByChartId(chartId);
        if (charts.size() != 0) {
            return updateDocument(chart);
        } else {
            ChartGen save = chartRepository.save(chart);
            return true;
        }
    }

    @Override
    public List<ChartGen> listDocuments(long userId) {
        return chartRepository.findAllByUserId(userId, PageRequest.of(3, 1));
    }

    @Override
    public Page<ChartGen> getChartList(ChartQueryRequest chartQueryRequest,HttpServletRequest httpServletRequest) {
        // page size
        // 页号 每一页的大小
        // 这个API的页号是从0开始的
        // 默认按照时间降序
        PageRequest pageRequest = PageRequest.of(chartQueryRequest.getCurrent() - 1, chartQueryRequest.getPageSize(), Sort.by("creatTime").descending());
        Long userId = chartQueryRequest.getUserId();
        User loginUser = userService.getLoginUser(httpServletRequest);
        if (userId == null) {
            userId = loginUser.getId();
        }
        String name = chartQueryRequest.getChartName();
        // 查找符合搜索名称的chart
        if (StringUtils.isNotBlank(name)) {
            // . 可以重复 0~n次 , 匹配所有满足的name
            String regex = ".*" + name + ".*";
            Query query = new Query();
            query.addCriteria(Criteria.where("userId").is(userId).and("name").regex(regex));
            query.with(pageRequest);
            List<ChartGen> charts = mongoTemplate.find(query, ChartGen.class);
            return excludeOldVersionAndBuildPage(charts, pageRequest);
        } else {
            List<ChartGen> charts = chartRepository.findAllByUserId(userId, pageRequest);
            return excludeOldVersionAndBuildPage(charts, pageRequest);
        }
    }

    @Override
    public ChartGen getChartByChartId(long chartId) {
        List<ChartGen> charts = chartRepository.findAllByChartId(chartId);
        if (charts.size() == 0) return null;
        if (charts.size() == 1) {
            return charts.get(0);
        }
        // 找到最大的版本
        int maxVersionIdx = 0;
        int maxVersion = Integer.MIN_VALUE;
        for (int i = 0; i < charts.size(); i++) {
            ChartGen chart = charts.get(i);
            if (chart.getVersion() > maxVersion) {
                maxVersionIdx = i;
                maxVersion = chart.getVersion();
            }
        }
        return charts.get(maxVersionIdx);
    }

    @Override
    public boolean insertChart(Chart chartEntity) {
        try {
            ChartGen chart = BeanUtil.copyProperties(chartEntity, ChartGen.class);
            chart.setChartId(chartEntity.getId());
            chart.setVersion(ChartGen.DEFAULT_VERSION);
            long chartId = chart.getChartId();
            Query query = new Query();
            query.addCriteria(Criteria.where("chartId").is(chartId));
            List<ChartGen> charts = mongoTemplate.find(query, ChartGen.class);
            // 是新的图表
            if (charts.size() == 0) {
                chartRepository.save(chart);
            } else {
                // 是需要更新的图表 : 获取新的版本号 => 保存
                int nextVersion = getNextVersion(charts);
                chart.setVersion(nextVersion);
                chartRepository.save(chart);
            }
            return true;
        } catch (RuntimeException e) {
            log.error("保存Chart到MongoDB失败 : {} , 异常信息:{} ", chartEntity, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteAllFromMongo(long id) {
        return chartRepository.deleteAllByChartId(id) != -1;
    }

    @Override
    public boolean updateDocument(ChartGen chart) {
        try {
            // 不设置ID ,使用MongoDB自动的ObjectId
            chart.setId(null);
            List<ChartGen> allByChartId = chartRepository.findAllByChartId(chart.getChartId());
            int nextVersion = getNextVersion(allByChartId);
            chart.setVersion(nextVersion);
            ChartGen save = chartRepository.save(chart);
            return true;
        } catch (RuntimeException e) {
            log.error("更新文档失败: {},{}", e, chart);
            return false;
        }
    }

    @Override
    public QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest,HttpServletRequest httpServletRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chartQueryRequest.getId();
        String name = chartQueryRequest.getChartName();
        String goal = chartQueryRequest.getGoal();
        String chartType = chartQueryRequest.getChartType();
        User loginUser = userService.getLoginUser(httpServletRequest);
        Long userId = loginUser.getId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(name), "chartName", name);
        queryWrapper.eq(StringUtils.isNotBlank(goal), "goal", goal);
        queryWrapper.eq(StringUtils.isNotBlank(chartType), "chartType", chartType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public boolean syncChart(Chart chartEntity, String genChart, String genResult) {
        ChartGen chart = BeanUtil.copyProperties(chartEntity, ChartGen.class);
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setChartId(chartEntity.getId());
        Long chartId = chart.getChartId();
        List<ChartGen> charts = chartRepository.findAllByChartId(chartId);
        if (charts.size() != 0) {
            return updateDocument(chart);
        } else {
            chart.setVersion(1);
            ChartGen save = chartRepository.save(chart);
            return true;
        }
    }

    @Override
    public boolean deleteSingleFromMongo(long id, int version) {
        Query query = new Query();
        query.addCriteria(Criteria.where("chartId").is(id));
        query.addCriteria(Criteria.where("version").is(version));
        DeleteResult remove = mongoTemplate.remove(query, Chart.class);
        // 按照前端的参数, 必定会存在一个对应的document , 如果没有就是删除失败了
        return remove.getDeletedCount() == 1;
    }

    @Override
    public BiResponse genChart(Chart chartEntity, ServerLoadInfo info) {
        GenChartStrategy genChartStrategy = strategySelector.selectStrategy(info);
        return genChartStrategy.executeGenChart(chartEntity);
    }

    /**
     * 获取下一个版本号
     *
     */
    private int getNextVersion(List<ChartGen> charts) {
        int maxVersion = Integer.MIN_VALUE;
        for (int i = 0; i < charts.size(); i++) {
            ChartGen chart = charts.get(i);
            if (chart.getVersion() > maxVersion) {
                maxVersion = chart.getVersion();
            }
        }
        return maxVersion + 1;
    }

    /**
     * 排除旧版本和构建返回Page
     *
     */
    private Page<ChartGen> excludeOldVersionAndBuildPage(List<ChartGen> charts, Pageable pageable) {
        long count = chartRepository.count();
        // 排除旧版本号Chart
        Map<Long, ChartGen> latestChartsMap = new HashMap<>();
        for (ChartGen chart : charts) {
            Long chartId = chart.getChartId();
            // 当chartId 相同时 , 获取version较大的chart
            if (!latestChartsMap.containsKey(chartId) || chart.getVersion() > latestChartsMap.get(chartId).getVersion()) {
                latestChartsMap.put(chartId, chart);
            }
        }
        return new PageImpl<>(new ArrayList<>(latestChartsMap.values()), pageable, count);
    }

//    @Override
//    public Page<Chart> getChartPageByRedis(ChartQueryRequest chartQueryRequest,HttpServletRequest httpServletRequest) {
//        long current = chartQueryRequest.getCurrent();
//        long size = chartQueryRequest.getPageSize();
//
//        User loginUser = userService.getLoginUser(httpServletRequest);
//        // 每个用户id唯一
//        String pageUser = CACHE_USER_PAGE + loginUser.getId();
//        // 根据需要查询的当前页和大小组合
//        String userPageArg = CACHE_PAGE_ARG + current+ ":" + size;
//        Object chartPageInfo = redisTemplate.opsForHash().get(pageUser, userPageArg);
//        if (chartPageInfo == null){
//            Page<Chart> chartPage = chartService.page(new Page<>(current, size),
//                    chartService.getQueryWrapper(chartQueryRequest));
//            redisTemplate.opsForHash().put(pageUser,userPageArg,chartPage);
//            redisTemplate.expire(pageUser,360 , TimeUnit.MINUTES);
//            return chartPage;
//        }
//
//        Object userChartPageInfo = redisTemplate.opsForHash().get(pageUser, userPageArg);
//        Page<Chart> cacheResult = RedisUtils.convertToPage(userChartPageInfo,Chart.class);
//        return cacheResult;
//    }


}




