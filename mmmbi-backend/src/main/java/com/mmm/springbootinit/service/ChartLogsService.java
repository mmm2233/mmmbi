package com.mmm.springbootinit.service;

import com.mmm.springbootinit.model.dto.ChartLogDTO;
import com.mmm.springbootinit.model.entity.Chart;
import com.mmm.springbootinit.model.entity.ChartLogs;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface ChartLogsService extends IService<ChartLogs> {
    /**
     * 记录生成日志
     *
     * @param chart
     */
    Long recordLog(Chart chart);

    /**
     * 获取过去 dayCount  天数的日志信息
     * @param dayCount
     * @return {@link List}<{@link ChartLogs}>
     */
    List<ChartLogDTO> getLogs(Integer dayCount, Long userId);
}
