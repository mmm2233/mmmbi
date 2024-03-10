package com.mmm.springbootinit.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.model.dto.chart
 * @Project：mmmbi-backend
 * @name：ChartRebuildRequest
 * @Date：2024/3/9 23:42
 * @Filename：ChartRebuildRequest
 */
@Data
public class ChartRebuildRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
