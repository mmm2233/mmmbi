package com.mmm.springbootinit.model.enums;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.model.enums
 * @Project：mmmbi-backend
 * @name：ChartStatusEnum
 * @Date：2024/3/12 13:58
 * @Filename：ChartStatusEnum
 */
public enum ChartStatusEnum {
    /**
     * 等待
     */
    WAIT("wait","生成等待中"),
    /**
     * 运行
     */
    RUNNING("running","正在执行生成..."),
    /**
     * 成功
     */
    SUCCEED("succeed","生成成功"),
    /**
     * 提起
     */
    FAILED("failed","图表生成失败");
    /**
     * 状态
     */
    final String status;

    /**
     * 执行信息
     */
    String message;

    ChartStatusEnum(String status, String msg) {
        this.status = status;
        this.message = msg;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
