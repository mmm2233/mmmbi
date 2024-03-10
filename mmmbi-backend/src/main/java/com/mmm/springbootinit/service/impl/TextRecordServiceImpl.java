package com.mmm.springbootinit.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmm.springbootinit.model.entity.TextRecord;
import com.mmm.springbootinit.service.TextRecordService;
import com.mmm.springbootinit.mapper.TextRecordMapper;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class TextRecordServiceImpl extends ServiceImpl<TextRecordMapper, TextRecord>
    implements TextRecordService{

    @Override
    public String buildUserInput(TextRecord textRecord, String textTaskType) {
        String textContent = textRecord.getTextContent();
        //构造用户输入
        StringBuilder userInput = new StringBuilder();
        String gold = "请使用"+textTaskType+"语法对下面文章格式化";

        userInput.append(gold).append("\n");

        if (StringUtils.isNotBlank(textContent)) {
            textContent = textContent.trim();
            userInput.append(textContent);
        }
        return userInput.toString();
    }
}




