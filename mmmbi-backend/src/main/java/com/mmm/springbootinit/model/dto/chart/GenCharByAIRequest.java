package com.mmm.springbootinit.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

/**
 *@Date：2024/3/2
 *@Author：mmm
 *@return：
 * 文件上传请求
 */
@Data
public class GenCharByAIRequest implements Serializable {

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表名称
     */
    private String chartName;

    /**
     * 图表类型
     */
    private String chartType;

    private static final long serialVersionUID = 1L;
}