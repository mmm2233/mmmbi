package com.mmm.springbootinit.model.dto.text;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.model.dto.text
 * @Project：mmmbi-backend
 * @name：TextRebuildRequest
 * @Date：2024/3/9 21:06
 * @Filename：TextRebuildRequest
 */

import lombok.Data;

import java.io.Serializable;

/**
 * 重新生成请求
 *
 */
@Data
public class TextRebuildRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
