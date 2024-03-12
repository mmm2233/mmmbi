package com.mmm.springbootinit.config;

import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.model.SparkMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.config
 * @Project：mmmbi-backend
 * @name：AiXunConfig
 * @Date：2024/3/10 15:53
 * @Filename：AiXunConfig
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ai.xun")
public class SparkAiConfig {

    private String appId;
    private String apiSecret;
    private String apiKey;

    @Bean
    public SparkClient sparkClient(){
        SparkClient sparkClient = new SparkClient();
        sparkClient.appid = appId;
        sparkClient.apiKey = apiKey;
        sparkClient.apiSecret = apiSecret;
        return sparkClient;
    }
}
