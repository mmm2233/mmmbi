package com.mmm.springbootinit.utils;

import org.apache.commons.lang3.StringUtils;

/**
 *@Date：2024/3/2
 *@Author：mmm
 *@return：
 * SQL 工具
 */
public class SqlUtils {

    /**
     * 校验排序字段是否合法（防止 SQL 注入）
     *
     * @param sortField
     * @return
     */
    public static boolean validSortField(String sortField) {
        if (StringUtils.isBlank(sortField)) {
            return false;
        }
        return !StringUtils.containsAny(sortField, "=", "(", ")", " ");
    }
}
