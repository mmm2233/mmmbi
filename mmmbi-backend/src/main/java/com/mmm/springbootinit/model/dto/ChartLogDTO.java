package com.mmm.springbootinit.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.model.dto
 * @Project：mmmbi-backend
 * @name：ChartLogDTO
 * @Date：2024/3/12 13:53
 * @Filename：ChartLogDTO
 */
@Data
public class ChartLogDTO {

    /**
     * 时间
     */
    private Date createTime;

    /**
     * 结果 : succeed or failed
     */
    private String result;

    /**
     * 次数
     */
    private Long count;
}
