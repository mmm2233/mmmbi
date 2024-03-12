package com.mmm.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.mmm.springbootinit.model.enums.PointChangeEnum;
import lombok.Data;

/**
 * 积分表
 * @TableName point_changes
 */
@TableName(value ="point_changes")
@Data
public class PointChanges implements Serializable {
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
     * 积分变动值
     */
    private Long changeAmount;

    /**
     * 积分变动类型
     */
    private Long changeType;

    /**
     * 积分变动原因
     */
    private String reason;

    /**
     * 积分变动后数量
     */
    private Long newPoints;

    /**
     * 积分来源
     */
    private String source;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Byte isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public PointChanges(PointChangeEnum pointChangeEnum, long userId) {
        this.changeAmount = (long)pointChangeEnum.getChangeAmount();
        this.source = pointChangeEnum.getSource();
        this.reason = pointChangeEnum.getReason();
        this.userId = userId;
        // 写错类型了不改了。。。。
        this.changeType = (long)pointChangeEnum.getChangeType().ordinal();
    }
}