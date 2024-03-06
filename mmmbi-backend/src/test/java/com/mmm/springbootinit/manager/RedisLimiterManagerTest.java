package com.mmm.springbootinit.manager;

import com.mmm.springbootinit.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.manager
 * @Project：mmmbi-backend
 * @name：RedisLimiterManagerTest
 * @Date：2024/3/6 12:34
 * @Filename：RedisLimiterManagerTest
 */
@SpringBootTest
class RedisLimiterManagerTest {

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Test
    void doRateLimit() throws InterruptedException {
//        String userId = "1";
//        try {
//            for (int i = 0; i < 5; i++) {
//                redisLimiterManager.doRateLimit(userId);
//                System.out.println("success");
//            }
//        }catch (BusinessException e){
//            System.out.println("请求频繁");
//        }
//
//        Thread.sleep(1000);
//
//        for (int i= 0;i<5;i++){
//            redisLimiterManager.doRateLimit(userId);
//            System.out.println("success");
//        }
    }
}