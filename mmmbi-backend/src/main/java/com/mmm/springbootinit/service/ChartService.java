package com.mmm.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmm.springbootinit.model.document.ChartGen;
import com.mmm.springbootinit.model.dto.chart.ChartQueryRequest;
import com.mmm.springbootinit.model.entity.Chart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mmm.springbootinit.model.vo.BiResponse;
import com.mmm.springbootinit.utils.ServerLoadInfo;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 */
public interface ChartService extends IService<Chart> {

    /**
     * 保存chart文档 : 当存在旧版本时自动设置为newVersion
     *
     */
    boolean saveDocument(ChartGen chart);

    /**
     * 列表文件
     *
     */
    List<ChartGen> listDocuments(long userId);

    /**
     * 查询图表Document
     *
     */
    Page<ChartGen> getChartList(ChartQueryRequest chartQueryRequest, HttpServletRequest httpServletRequest);


    /**
     * 通过ChartId 获取 Chart(latest version)
     *
     */
    ChartGen getChartByChartId(long chartId);

    /**
     * 插入Chart
     *
     */
    boolean insertChart(Chart chartEntity);

    /**
     * 从mongo删除Chart
     *
     */
    boolean deleteAllFromMongo(long id);


    /**
     * 从mongo更新Chart : 创建新的版本
     *
     */
    boolean updateDocument(ChartGen chart);

    QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest,HttpServletRequest httpServletRequest);

    /**
     * 同步Chart数据到MongoDB
     *
     */
    boolean syncChart(Chart chartEntity,String genChart ,String genResult);

    /**
     * 删除当前id对应的最新版本的document
     */
    boolean deleteSingleFromMongo(long id,int version);

    /**
     * 通过AI生成图表
     *
     */
    BiResponse genChart(Chart chartEntity, ServerLoadInfo info);
}