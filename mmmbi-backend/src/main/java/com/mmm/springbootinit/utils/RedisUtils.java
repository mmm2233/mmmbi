package com.mmm.springbootinit.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;
import java.util.Map;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.utils
 * @Project：mmmbi-backend
 * @name：RedisUtils
 * @Date：2024/3/8 21:01
 * @Filename：RedisUtils
 */

public class RedisUtils {

    @SuppressWarnings("unchecked")
    public static <T> Page<T> convertToPage(Object obj,Class<T> clazz){
        if (obj != null && obj instanceof Map) {
            Map<String ,Object> resultMap = (Map<String ,Object>) obj;
            if (resultMap.containsKey("records")){
                Page<T> page = new Page<>();
                page.setRecords((List<T>) resultMap.get("records"));
                page.setCurrent(((Number) resultMap.getOrDefault("current",0)).longValue());
                page.setSize(((Number) resultMap.getOrDefault("size",0)).longValue());
                page.setTotal(((Number) resultMap.getOrDefault("total",0)).longValue());
                return page;
            }
        }
        return null;
    }
}
