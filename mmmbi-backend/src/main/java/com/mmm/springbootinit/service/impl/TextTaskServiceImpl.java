package com.mmm.springbootinit.service.impl;

import cn.hutool.core.io.FileUtil;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.constant.CreditConstant;
import com.mmm.springbootinit.constant.GenConstant;
import com.mmm.springbootinit.exception.ThrowUtils;
import com.mmm.springbootinit.model.dto.text.GenTextTaskByAiRequest;
import com.mmm.springbootinit.model.entity.TextRecord;
import com.mmm.springbootinit.model.entity.TextTask;
import com.mmm.springbootinit.model.entity.User;
import com.mmm.springbootinit.service.CreditService;
import com.mmm.springbootinit.service.TextRecordService;
import com.mmm.springbootinit.service.TextTaskService;
import com.mmm.springbootinit.mapper.TextTaskMapper;
import com.mmm.springbootinit.utils.TxtUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
@Service
public class TextTaskServiceImpl extends ServiceImpl<TextTaskMapper, TextTask>
    implements TextTaskService{

    @Resource
    private CreditService creditService;

    @Resource
    private TextRecordService textRecordService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TextTask getTextTask(MultipartFile multipartFile, GenTextTaskByAiRequest genTextTaskByAiRequest, User loginUser) {
        String textTaskType = genTextTaskByAiRequest.getTextType();
        String name = genTextTaskByAiRequest.getName();
        //校验
        ThrowUtils.throwIf(StringUtils.isBlank(textTaskType), ErrorCode.PARAMS_ERROR,"目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name)&&name.length()>=100,ErrorCode.PARAMS_ERROR,"名称过长");
        //校验文件
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        final long ONE_MB = 1024*1024;
        ThrowUtils.throwIf(size>ONE_MB,ErrorCode.PARAMS_ERROR,"文件超过1MB");
        ThrowUtils.throwIf(size==0,ErrorCode.PARAMS_ERROR,"文件为空");
        //校验文件后缀
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffix = Arrays.asList("txt");
        ThrowUtils.throwIf(!validFileSuffix.contains(suffix),ErrorCode.PARAMS_ERROR,"文件后缀名非法");

        //消耗积分
        Boolean creditResult = creditService.consumeCredits(loginUser.getId(), CreditConstant.CREDIT_TEXT_SUCCESS);
        ThrowUtils.throwIf(!creditResult,ErrorCode.OPERATION_ERROR,"你的积分不足");

        //保存数据库 wait
        //保存任务进数据库
        TextTask textTask = new TextTask();
        textTask.setTextType(textTaskType);
        textTask.setName(name);
        textTask.setUserId(loginUser.getId());
        textTask.setStatus(GenConstant.WAIT);
        boolean saveResult = this.save(textTask);
        ThrowUtils.throwIf(!saveResult,ErrorCode.SYSTEM_ERROR,"文本任务保存失败");

        Long taskId = textTask.getId();
        // 压缩后的数据
        ArrayList<String> textContentList = TxtUtils.readerFile(multipartFile);
        ThrowUtils.throwIf(textContentList.size() ==0,ErrorCode.PARAMS_ERROR,"文件为空");

        //将分割的内容保存入记录表
        ArrayList<TextRecord> taskArrayList = new ArrayList<>();
        textContentList.forEach(textContent ->{
            TextRecord textRecord = new TextRecord();
            textRecord.setTextTaskId(taskId);
            textRecord.setTextContent(textContent);
            textRecord.setStatus(GenConstant.WAIT);
            taskArrayList.add(textRecord);
        });

        boolean batchResult = textRecordService.saveBatch(taskArrayList);
        ThrowUtils.throwIf(!batchResult, ErrorCode.SYSTEM_ERROR,"文本记录保存失败");

        return textTask;
    }

    @Override
    public void handleTextTaskUpdateError(Long textTaskId, String execMessage) {
        TextTask updateTextTaskResult = new TextTask();
        updateTextTaskResult.setStatus(GenConstant.FAILED);
        updateTextTaskResult.setId(textTaskId);
        updateTextTaskResult.setExecMessage(execMessage);
        boolean updateResult = this.updateById(updateTextTaskResult);
        if (!updateResult){
            log.error("更新文本失败状态失败"+textTaskId+","+execMessage);
        }
    }
}




