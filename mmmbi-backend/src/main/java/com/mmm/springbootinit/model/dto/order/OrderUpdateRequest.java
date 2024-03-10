package com.mmm.springbootinit.model.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.model.dto.order
 * @Project：mmmbi-backend
 * @name：OrderUpdateRequest
 * @Date：2024/3/9 22:21
 * @Filename：OrderUpdateRequest
 */
@Data
public class OrderUpdateRequest implements Serializable {

    /**
     * 订单id
     */
    private Long id;

    /**
     * 支付宝交易凭证id
     */
    private String alipayTradeNo;

    /**
     * unpaid,paying,succeed,failed
     */
    private String tradeStatus;

    /**
     * 支付宝买家id
     */
    private String buyerId;


    private static final long serialVersionUID = 1L;
}
