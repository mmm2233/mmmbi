package com.mmm.springbootinit.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.model.enums
 * @Project：mmmbi-backend
 * @name：FutureStatus
 * @Date：2024/3/6 16:20
 * @Filename：FutureStatus
 */
public enum FutureStatus {
    WAIT("等待中","wait"),
    RUNING("运行中","running"),
    SUCCEED("执行成功","succeed"),
    FAILED("执行失败","failed");


    private final String text;

    private final String value;

    FutureStatus(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static FutureStatus getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (FutureStatus anEnum : FutureStatus.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
