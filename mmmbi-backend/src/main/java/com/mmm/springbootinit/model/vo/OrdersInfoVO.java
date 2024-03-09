package com.mmm.springbootinit.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.model.vo
 * @Project：mmmbi-backend
 * @name：OrdersInfoVO
 * @Date：2024/3/9 16:40
 * @Filename：OrdersInfoVO
 */
@Data
public class OrdersInfoVO {
    /**
     * id
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 支付宝流水账号
     */
    private String alipayAccountNo;

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
     * 交易状态【0->未支付；1->已完成；2->支付失败】
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

    private String qrCode;

    /**
     * 是否删除
     */
    private Byte isDelete;
}
