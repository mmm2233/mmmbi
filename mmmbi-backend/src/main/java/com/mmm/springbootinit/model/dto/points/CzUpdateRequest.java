package com.mmm.springbootinit.model.dto.points;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.model.dto.points
 * @Project：mmmbi-backend
 * @name：CzUpdateRequest
 * @Date：2024/3/13 10:20
 * @Filename：CzUpdateRequest
 */
@Data
public class CzUpdateRequest implements Serializable {

    private Long userId;

    private Integer amount;

    private static final long serialVersionUID = 1L;
}
