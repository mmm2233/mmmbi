package com.mmm.springbootinit.config;

import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.exception.SparkException;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.SparkSyncChatResponse;
import io.github.briqt.spark4j.model.request.SparkRequest;
import io.github.briqt.spark4j.model.response.SparkTextUsage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.config
 * @Project：mmmbi-backend
 * @name：AiXunConfigTest
 * @Date：2024/3/10 16:05
 * @Filename：AiXunConfigTest
 */
@SpringBootTest
class SparkAiConfigTest {

    @Resource
    SparkClient sparkClient;

    @Test
    public void sparkTest() {
        // 消息列表，可以在此列表添加历史对话记录
        List<SparkMessage> messages=new ArrayList<>();
        messages.add(SparkMessage.systemContent("请你扮演我的语文老师李老师，问我讲解问题问题，希望你可以保证知识准确，逻辑严谨。"));
        messages.add(SparkMessage.userContent("鲁迅和周树人小时候打过架吗？"));
// 构造请求
        SparkRequest sparkRequest=SparkRequest.builder()
// 消息列表
                .messages(messages)
// 模型回答的tokens的最大长度,非必传，默认为2048。
// V1.5取值为[1,4096]
// V2.0取值为[1,8192]
// V3.0取值为[1,8192]
                .maxTokens(2048)
// 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高 非必传,取值为[0,1],默认为0.5
                .temperature(0.2)
                // 指定请求版本，默认使用最新3.0版本
                .apiVersion(SparkApiVersion.V3_5)
                .build();

        try {
            // 同步调用
            SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
            SparkTextUsage textUsage = chatResponse.getTextUsage();

            System.out.println("\n回答：" + chatResponse.getContent());
            System.out.println("\n提问tokens：" + textUsage.getPromptTokens()
                    + "，回答tokens：" + textUsage.getCompletionTokens()
                    + "，总消耗tokens：" + textUsage.getTotalTokens());
        } catch (SparkException e) {
            System.out.println("发生异常了：" + e.getMessage());
        }

    }

    @Test
    public void sparkAsyncTest() {
        // 消息列表，可以在此列表添加历史对话记录
        List<SparkMessage> messages=new ArrayList<>();
        messages.add(SparkMessage.userContent("请你扮演我的语文老师李老师，问我讲解问题问题，希望你可以保证知识准确，逻辑严谨。"));
        messages.add(SparkMessage.assistantContent("好的，这位同学，有什么问题需要李老师为你解答吗？"));
        messages.add(SparkMessage.userContent("鲁迅和周树人小时候打过架吗？"));

        // 构造请求
        SparkRequest sparkRequest=SparkRequest.builder()
        // 消息列表
                .messages(messages)
        // 模型回答的tokens的最大长度,非必传，默认为2048。
        // V1.5取值为[1,4096]
        // V2.0取值为[1,8192]
        // V3.0取值为[1,8192]
                .maxTokens(2048)
        // 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高 非必传,取值为[0,1],默认为0.5
                .temperature(0.2)
                .apiVersion(SparkApiVersion.V3_5)
                .build();

        // 使用默认的控制台监听器，流式调用；
        // 实际使用时请继承SparkBaseListener自定义监听器实现
        SparkConsoleListener sparkConsoleListener = new SparkConsoleListener();
        sparkClient.chatStream(sparkRequest,sparkConsoleListener);

    }
}