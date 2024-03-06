package com.mmm.springbootinit.controller;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 队列测试
 * @Author：mmm
 * @Package：com.mmm.springbootinit.controller
 * @Project：mmmbi-backend
 * @name：QueueController
 * @Date：2024/3/6 15:18
 * @Filename：QueueController
 */

@RestController
@RequestMapping("/queue")
@Data
@Slf4j
@Profile({"dev","local"})
public class QueueController {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @GetMapping("/add")
    public void add(String name) throws InterruptedException {
        CompletableFuture.runAsync(()->{
            log.info("任务执行中" + name + ".执行人" + Thread.currentThread().getName());
            //System.out.println("任务执行中" + name + ".执行人" + Thread.currentThread().getName());
            try {
                Thread.sleep(600000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, threadPoolExecutor);
    }

    @GetMapping("/get")
    public String  get(){
        Map<String ,Object> map =new HashMap<>();

        int size = threadPoolExecutor.getQueue().size();
        map.put("队列长度：",size);
        long taskCount = threadPoolExecutor.getTaskCount();
        map.put("任务总数：",taskCount);
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
        map.put("已完成的任务数： ",completedTaskCount);
        int activeCount = threadPoolExecutor.getActiveCount();
        map.put("正在工作的线程数：",activeCount);

        return JSONUtil.toJsonStr(map);
    }
}
