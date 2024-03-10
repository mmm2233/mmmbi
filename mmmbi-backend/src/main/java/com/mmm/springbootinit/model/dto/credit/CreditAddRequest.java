package com.mmm.springbootinit.model.dto.credit;

import lombok.Data;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.model.dto.credit
 * @Project：mmmbi-backend
 * @name：CreditAddRequest
 * @Date：2024/3/9 23:39
 * @Filename：CreditAddRequest
 */
@Data
public class CreditAddRequest {
    /**
     * 创建用户Id
     */
    private Long userId;

    /**
     * 总积分
     */
    private Long creditTotal;

    private static final long serialVersionUID = 1L;
}
