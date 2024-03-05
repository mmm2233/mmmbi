package com.mmm.springbootinit.manager;

import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.exception.BusinessException;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.manager
 * @Project：mmmbi-backend
 * @name：AIManager
 * @Date：2024/3/5 16:10
 * @Filename：AIManager
 */
@Component
public class AIManager {
    @Resource
    private YuCongMingClient yuCongMingClient;

    /**
     * AI对话
     *@Date：2024/3/5
     *@Author：mmm
     *@return：
     *
     */
    public String doChat(long modeId,String message){
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(modeId);
        devChatRequest.setMessage(message);
        BaseResponse<DevChatResponse> response = yuCongMingClient.doChat(devChatRequest);
        if (response == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"AI响应错误");
        }
        return response.getData().getContent();
    }
}
