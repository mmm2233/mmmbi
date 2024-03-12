package com.mmm.springbootinit.constant;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.constant
 * @Project：mmmbi-backend
 * @name：RedisConstant
 * @Date：2024/3/12 18:03
 * @Filename：RedisConstant
 */
public interface RedisConstant {
    String INTERFACE_RANK_KEY = "interface:rank";

    String VERIFY_CODE_KEY = "user:verifycode:";

    long VERIFY_CODE_TTL = 60*5L;

    String GEN_CHART_LIMIT_KEY = "gen:chart:limit:";
}
