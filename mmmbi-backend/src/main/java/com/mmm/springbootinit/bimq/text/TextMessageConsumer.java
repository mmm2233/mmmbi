package com.mmm.springbootinit.bimq.text;

import cn.hutool.core.date.DateTime;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.constant.MqConstant;
import com.mmm.springbootinit.constant.GenConstant;
import com.mmm.springbootinit.exception.BusinessException;
import com.mmm.springbootinit.manager.AiManager;
import com.mmm.springbootinit.model.entity.TextRecord;
import com.mmm.springbootinit.model.entity.TextTask;
import com.mmm.springbootinit.service.TextRecordService;
import com.mmm.springbootinit.service.TextTaskService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 文本转换消费者队列
 * @Author：mmm
 * @Package：com.mmm.springbootinit.bimq.text
 * @Project：mmmbi-backend
 * @name：TextMessageConsumer
 * @Date：2024/3/9 21:11
 * @Filename：TextMessageConsumer
 */
@Component
@Slf4j
public class TextMessageConsumer {
    @Resource
    private TextTaskService textTaskService;

    @Resource
    private TextRecordService textRecordService;
    @Resource
    private AiManager aiManager;

    @SneakyThrows
    @RabbitListener(queues = {MqConstant.TEXT_QUEUE_NAME},ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag){
        log.warn("接收到队列信息，receiveMessage={}=======================================",message);
        if (StringUtils.isBlank(message)){
            //消息为空，消息拒绝，不重复发送，不重新放入队列
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"消息为空");
        }

        long textTaskId = Long.parseLong(message);
        List<TextRecord> textRecordList = textRecordService.list(new QueryWrapper<TextRecord>().eq("textTaskId", textTaskId));
        //todo 可以将记录表字段增加一个type 减少一次查询表
        TextTask textTask = textTaskService.getById(textTaskId);
        if (textRecordList == null){
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"文本为空");
        }
        //修改表状态为执行中，执行成功修改为“已完成”；执行失败修改为“失败”
        TextTask updateTask = new TextTask();
        updateTask.setId(textTaskId);
        updateTask.setStatus(GenConstant.RUNNING);
        boolean updateResult = textTaskService.updateById(updateTask);
        if (!updateResult){
            textTaskService.handleTextTaskUpdateError(textTaskId,"更新图表执行状态失败");
            return;
        }
        //调用AI

        for (TextRecord textRecord : textRecordList) {
            String result = null;
            //队列重新消费时，不在重新生成已经生成过的数据
            if (textRecord.getGenTextContent() != null) continue;
            try {
                result = aiManager.doChat(GenConstant.MODE_TEXT_ID,textRecordService.buildUserInput(textRecord,textTask.getTextType()));
            } catch (Exception e) {
                channel.basicNack(deliveryTag,false,true);
                log.warn("信息放入队列{}", DateTime.now());
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"AI 服务错误");
            }
            textRecord.setGenTextContent(result);
            textRecord.setStatus(GenConstant.SUCCEED);
            boolean updateById = textRecordService.updateById(textRecord);
            if (!updateById){
                log.warn("AI生成错误，重新放入队列");
                channel.basicNack(deliveryTag,false,true);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"保存失败");
            }
        }
        //将记录表中已经生成好的内容合并存入任务表
        StringBuilder stringBuilder = new StringBuilder();
        textRecordList.forEach(textRecord1 -> {
            stringBuilder.append(textRecord1.getGenTextContent()).append('\n');
        });
        TextTask textTask1 = new TextTask();
        textTask1.setId(textTaskId);
        textTask1.setGenTextContent(stringBuilder.toString());
        textTask1.setStatus(GenConstant.SUCCEED);
        boolean save = textTaskService.updateById(textTask1);
        if (!save){
            channel.basicNack(deliveryTag,false,true);
            textTaskService.handleTextTaskUpdateError(textTask.getId(), "ai返回文本任务保存失败");
        }

        //消息确认
        channel.basicAck(deliveryTag,false);
    }
}
