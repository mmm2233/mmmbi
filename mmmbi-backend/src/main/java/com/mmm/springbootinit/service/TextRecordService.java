package com.mmm.springbootinit.service;

import com.mmm.springbootinit.model.entity.TextRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface TextRecordService extends IService<TextRecord> {
    /**
     * 文本用户输入构造
     * @param textRecord
     * @param textTaskType
     * @return
     */
    String buildUserInput(TextRecord textRecord,String textTaskType);
}
