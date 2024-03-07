package com.mmm.springbootinit.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.config
 * @Project：mmmbi-backend
 * @name：ThreadPoolExecutorConfig
 * @Date：2024/3/6 14:30
 * @Filename：ThreadPoolExecutorConfig
 */

@Configuration
public class ThreadPoolExecutorConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        ThreadFactory threadFactory =new ThreadFactory() {
            private int count =1;

            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("线程：" + count++);
                return thread;
            }
        };

        //todo 反向压力:https://zhuanlan.zhihu.com/p/404993753 当第三方AI接口的调用服务状态，比如通过AI任务队列数来控制系统的核心线程数，
        // 可以用定时任务
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 4, 100, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(4), threadFactory);

        return threadPoolExecutor;
    }
}
