package com.mmm.springbootinit.model.enums;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.model.enums
 * @Project：mmmbi-backend
 * @name：PointChangeEnum
 * @Date：2024/3/12 13:49
 * @Filename：PointChangeEnum
 */
public enum PointChangeEnum {

    DAILY_LOGIN_ADD("SYSTEM", 50, "每日登录奖励", ChangeType.INCREASE),
    CHAT_DEDUCT("SYSTEM", 2, "在线提问扣除", ChangeType.DECREASE),
    GEN_CHART_DEDUCT("SYSTEM", 5, "生成图表扣除", ChangeType.DECREASE),
    GEN_CHART_FAILED_COMPENSATE("SYSTEM", 5, "生成图表失败补偿", ChangeType.DECREASE),
    GEN_CHART_FAILED_ADD("SYSTEM", 3, "生成图表失败补偿", ChangeType.DECREASE),
    CZ_ALIPAY_ADD("CZ", 0, "支付宝充值", ChangeType.INCREASE);

    /**
     * 改变来源
     */
    private String source;

    /**
     * 变化量
     */
    private int changeAmount;

    /**
     * 原因说明
     */
    private String reason;

    /**
     * 改变类型(增加or减少)
     */
    private ChangeType changeType;

    PointChangeEnum(String source, int changeAmount, String reason, ChangeType changeType) {
        this.source = source;
        this.changeAmount = changeAmount;
        this.changeType = changeType;
        this.reason = reason;
    }

    public String getSource() {
        return source;
    }

    public int getChangeAmount() {
        return changeAmount;
    }

    public String getReason() {
        return reason;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeAmount(Integer amount) {
        this.changeAmount = amount;
    }

    /**
     * 变化类型
     *
     * @date 2023/11/19
     */
    public enum ChangeType {
        INCREASE,
        DECREASE
    }
}
