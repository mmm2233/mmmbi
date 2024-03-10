package com.mmm.springbootinit.model.dto.credit;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.model.dto.credit
 * @Project：mmmbi-backend
 * @name：CreditUpdateRequest
 * @Date：2024/3/9 23:42
 * @Filename：CreditUpdateRequest
 */
@Data
public class CreditUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 总积分
     */
    private Long creditTotal;


    private static final long serialVersionUID = 1L;
}