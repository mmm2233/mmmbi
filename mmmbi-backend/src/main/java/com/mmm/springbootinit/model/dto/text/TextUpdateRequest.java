package com.mmm.springbootinit.model.dto.text;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.model.dto.text
 * @Project：mmmbi-backend
 * @name：TextUpdateRequest
 * @Date：2024/3/9 21:07
 * @Filename：TextUpdateRequest
 */
@Data
public class TextUpdateRequest implements Serializable {

    /**
     * 任务id
     */
    private Long id;

    /**
     * 笔记名称
     */
    private String name;

    /**
     * 文本类型
     */
    private String textType;

    /**
     * 生成的文本内容
     */
    private String genTextContent;

    /**
     * wait,running,succeed,failed
     */
    private String status;

    /**
     * 执行信息
     */
    private String execMessage;



    private static final long serialVersionUID = 1L;
}
