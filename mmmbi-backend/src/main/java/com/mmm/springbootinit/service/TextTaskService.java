package com.mmm.springbootinit.service;

import com.mmm.springbootinit.model.dto.text.GenTextTaskByAiRequest;
import com.mmm.springbootinit.model.entity.TextTask;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mmm.springbootinit.model.entity.User;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 */
public interface TextTaskService extends IService<TextTask> {
    /**
     * 获取准备分析的表数据(事务回滚)
     * @param multipartFile
     * @param genTextTaskByAiRequest
     * @param loginUser
     * @return
     */
    TextTask getTextTask(MultipartFile multipartFile, GenTextTaskByAiRequest genTextTaskByAiRequest, User loginUser);

    /**
     * 文本更新失败
     * @param textTaskId
     * @param execMessage
     */
    void handleTextTaskUpdateError(Long textTaskId, String execMessage);
}
