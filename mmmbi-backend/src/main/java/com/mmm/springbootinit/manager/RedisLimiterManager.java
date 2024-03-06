package com.mmm.springbootinit.manager;

import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 专门提供 RedisLimiter 限流基础服务的（提供了通用的能力）
 * @Author：mmm
 * @Package：com.mmm.springbootinit.manager
 * @Project：mmmbi-backend
 * @name：RedisLimiterManager
 * @Date：2024/3/6 12:15
 * @Filename：RedisLimiterManager
 */

@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * key 区分不同的限流器，比如不同用户的ip分别统计
     *@Date：2024/3/6
     *@Author：mmm
     *@return：
     *
     */
    public void doRateLimit(String key){
        // 创建一个每秒允许 1 个操作的限流器
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);

        //当有操作来了，请求一个令牌.应用场景当网站分为VIP和普通用户，VIP每秒可以访问5次，普通用户每秒1次，就可以一次请求耗费不同的令牌数来限流
        boolean     canOp = rateLimiter.tryAcquire(1);

        if (!canOp){
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }
}
