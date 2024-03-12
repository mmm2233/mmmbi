package com.mmm.springbootinit.model.dto.chart;

import lombok.Data;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.model.dto.chart
 * @Project：mmmbi-backend
 * @name：DeleteChartDocRequest
 * @Date：2024/3/12 18:14
 * @Filename：DeleteChartDocRequest
 */
@Data
public class DeleteChartDocRequest {

    private long id;

    private int version;
}
