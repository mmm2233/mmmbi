package com.mmm.springbootinit.model.dto.image;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.model.dto.image
 * @Project：mmmbi-backend
 * @name：AddImageRequest
 * @Date：2024/3/10 20:12
 * @Filename：AddImageRequest
 */
@Data
public class AddImageRequest implements Serializable {
    /**
     * 业务
     */
    private String biz;

    /**
     * 问题
     */
    private String goal;

    private static final long serialVersionUID = 1L;
}
