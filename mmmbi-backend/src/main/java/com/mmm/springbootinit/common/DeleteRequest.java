package com.mmm.springbootinit.common;

import java.io.Serializable;
import lombok.Data;

/**
*@Date：2024/3/2
*@Author：mmm
*@return：
* 删除请求
*/
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}