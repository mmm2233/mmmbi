package com.mmm.springbootinit.model.dto.file;

import java.io.Serializable;
import lombok.Data;

/**
 *@Date：2024/3/2
 *@Author：mmm
 *@return：
 * 文件上传请求
 */
@Data
public class UploadFileRequest implements Serializable {

    /**
     * 业务
     */
    private String biz;

    private static final long serialVersionUID = 1L;
}