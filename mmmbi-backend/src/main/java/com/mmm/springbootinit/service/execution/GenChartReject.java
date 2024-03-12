package com.mmm.springbootinit.service.execution;

import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.exception.BusinessException;
import com.mmm.springbootinit.model.entity.Chart;
import com.mmm.springbootinit.model.vo.BiResponse;
import com.mmm.springbootinit.service.GenChartStrategy;
import org.springframework.stereotype.Component;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.service.execution
 * @Project：mmmbi-backend
 * @name：GenChartReject
 * @Date：2024/3/12 12:34
 * @Filename：GenChartReject
 */
@Component(value = "gen_reject")
public class GenChartReject implements GenChartStrategy {
    @Override
    public BiResponse executeGenChart(Chart chart) {
        throw new BusinessException(ErrorCode.TOO_MANY_REQUEST, "服务器繁忙,请稍后重试!");
    }
}
