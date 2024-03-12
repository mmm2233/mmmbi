package com.mmm.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 积分表
 * @TableName points
 */
@TableName(value ="points")
@Data
public class Points implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建用户Id
     */
    private Long userId;

    /**
     * 剩余积分数量
     */
    private Long remainingPoints;

    /**
     * 总积分数量
     */
    private Long totalPoints;

    /**
     * 总积分
     */
    private Long creditTotal;

    /**
     * 积分状态，有效1无效0
     */
    private Byte status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Byte isDelete;

    public enum Status {
        EXPIRED,
        VALID
    }

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}