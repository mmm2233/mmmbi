package com.mmm.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 订单详情表
 * @TableName ordersinfo
 */
@TableName(value ="ordersinfo")
@Data
public class Ordersinfo implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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

    /**
     * 是否删除
     */
    private Byte isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}