package com.mmm.springbootinit.common;

import com.mmm.springbootinit.constant.CommonConstant;
import lombok.Data;

/**
*@Date：2024/3/2
*@Author：mmm
*@return：
* 分页请求
*/
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int current = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     */
    private String sortOrder = CommonConstant.SORT_ORDER_ASC;
}
