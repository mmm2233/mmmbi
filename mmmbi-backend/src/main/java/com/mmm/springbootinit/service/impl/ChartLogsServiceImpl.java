package com.mmm.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmm.springbootinit.model.dto.ChartLogDTO;
import com.mmm.springbootinit.model.entity.Chart;
import com.mmm.springbootinit.model.entity.ChartLogs;
import com.mmm.springbootinit.model.enums.ChartStatusEnum;
import com.mmm.springbootinit.service.ChartLogsService;
import com.mmm.springbootinit.mapper.ChartLogsMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class ChartLogsServiceImpl extends ServiceImpl<ChartLogsMapper, ChartLogs>
    implements ChartLogsService{

    @Override
    public Long recordLog(Chart chart) {
        ChartLogs ChartLogs = new ChartLogs();
        ChartLogs.setUserId(chart.getUserId());
        ChartLogs.setChartId(chart.getId());
        if (chart.getStatus().equals(ChartStatusEnum.SUCCEED.getStatus())) {
            ChartLogs.setGenResult(ChartStatusEnum.SUCCEED.getStatus());
        } else {
            ChartLogs.setGenResult(ChartStatusEnum.FAILED.getStatus());
        }
        save(ChartLogs);
        return ChartLogs.getId();
    }

    @Override
    public List<ChartLogDTO> getLogs(Integer dayCount, Long userId) {
        return null;
    }
}




