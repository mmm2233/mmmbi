package com.mmm.springbootinit.service;

import com.mmm.springbootinit.model.document.ChartGen;
import com.mmm.springbootinit.model.entity.Chart;
import com.mmm.springbootinit.model.vo.BiResponse;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.service
 * @Project：mmmbi-backend
 * @name：GenChartStrategy
 * @Date：2024/3/12 12:14
 * @Filename：GenChartStrategy
 */
public interface GenChartStrategy {
    /**
     * 执行图表生成
     *
     * @param chart 表实体
     * @return {@link BiResponse}
     */
    BiResponse executeGenChart(Chart chart);
}
