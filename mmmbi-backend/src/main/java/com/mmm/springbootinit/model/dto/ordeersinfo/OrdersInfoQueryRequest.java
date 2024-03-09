package com.mmm.springbootinit.model.dto.ordeersinfo;

import com.mmm.springbootinit.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.model.dto.ordeersinfo
 * @Project：mmmbi-backend
 * @name：OrdersInfoQueryRequest
 * @Date：2024/3/9 15:39
 * @Filename：OrdersInfoQueryRequest
 */
@Data
public class OrdersInfoQueryRequest  extends PageRequest implements Serializable {

    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 支付宝流水账号
     */
    private Long alipayAccountNo;

    /**
     * 支付宝唯一id
     */
    private String alipayId;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 交易金额
     */
    private Double totalAmount;

    /**
     * 交易状态【0->待付款；1->已完成；2->无效订单】
     */
    private Integer payStatus;

    /**
     * 支付时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    private static final long serialVersionUID = 1L;
}
