package com.mmm.springbootinit.model.dto.credit;

import com.mmm.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.model.dto.credit
 * @Project：mmmbi-backend
 * @name：CreditQueryRequest
 * @Date：2024/3/9 23:40
 * @Filename：CreditQueryRequest
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CreditQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 创建用户Id
     */
    private Long userId;

    /**
     * 总积分
     */
    private Long creditTotal;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
