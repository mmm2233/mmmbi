package com.mmm.springbootinit.model.enums;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.model.enums
 * @Project：mmmbi-backend
 * @name：GenChartStrategyEnum
 * @Date：2024/3/12 12:18
 * @Filename：GenChartStrategyEnum
 */
public enum  GenChartStrategyEnum {

    GEN_MQ("gen_mq"),
    GEN_SYNC("gen_sync"),
    GEN_THREAD_POOL("gen_thread_pool"),
    GEN_REJECT("gen_reject");

    private String value;

    GenChartStrategyEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
