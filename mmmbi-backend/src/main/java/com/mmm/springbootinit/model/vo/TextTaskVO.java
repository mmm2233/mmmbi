package com.mmm.springbootinit.model.vo;
import lombok.Data;

import java.util.Date;
/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.model.vo
 * @Project：mmmbi-backend
 * @name：TextTaskVO
 * @Date：2024/3/9 21:46
 * @Filename：TextTaskVO
 */

@Data
public class TextTaskVO {

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
     * 创建时间
     */
    private Date createTime;


    private static final long serialVersionUID = 1L;
}
