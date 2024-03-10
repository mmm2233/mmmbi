package com.mmm.springbootinit.model.dto.credit;

import lombok.Data;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.model.dto.credit
 * @Project：mmmbi-backend
 * @name：CreditEditRequest
 * @Date：2024/3/9 23:40
 * @Filename：CreditEditRequest
 */
@Data
public class CreditEditRequest {
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
