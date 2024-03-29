package com.mmm.springbootinit.constant;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.constant
 * @Project：mmmbi-backend
 * @name：CreditConstant
 * @Date：2024/3/9 8:59
 * @Filename：CreditConstant
 */
public interface CreditConstant {

    /**
     * 每日签到积分
     */
    Long CREDIT_DAILY = 100L;

    /**
     *
     */
    Long CREDIT_TIME = -20L;

    /**
     *
     */
    Long CREDIT_TIME_1 = 20L;

    /**
     * 生成文本消耗积分
     */
    long CREDIT_TEXT_SUCCESS = -10;

    /**
     * 生成文本失败返回积分
     */
    long CREDIT_TEXT_FALSE = 10;
}
