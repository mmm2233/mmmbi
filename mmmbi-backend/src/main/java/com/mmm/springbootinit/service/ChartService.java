package com.mmm.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmm.springbootinit.model.dto.chart.ChartQueryRequest;
import com.mmm.springbootinit.model.entity.Chart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mmm.springbootinit.model.entity.Chart;
import com.mmm.springbootinit.model.vo.ChartVO;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public interface ChartService extends IService<Chart> {
    /**
     * 校验
     *
     * @param chart
     * @param add
     */
    void validChart(Chart chart, boolean add);

    /**
     * 获取查询条件
     *
     * @param chartQueryRequest
     * @return
     */
    QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest);

    /**
     * 获取帖子封装
     *
     * @param chart
     * @param request
     * @return
     */
    ChartVO getChartVO(Chart chart, HttpServletRequest request);

    /**
     * 分页获取帖子封装
     *
     * @param chartPage
     * @param request
     * @return
     */
    Page<ChartVO> getChartVOPage(Page<Chart> chartPage, HttpServletRequest request);
}
