package com.mmm.springbootinit.model.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建充值订单表
 * @Author：mmm
 * @Package：com.mmm.springbootinit.model.dto.order
 * @Project：mmmbi-backend
 * @name：OrderAddRequest
 * @Date：2024/3/9 22:20
 * @Filename：OrderAddRequest
 */
@Data
public class OrderAddRequest implements Serializable {

    /**
     * 交易名称
     */
    private String subject;

    /**
     * 交易金额
     */
    private Double totalAmount;

    private static final long serialVersionUID = 1L;
}