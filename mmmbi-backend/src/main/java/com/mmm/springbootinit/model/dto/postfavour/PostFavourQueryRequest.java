package com.mmm.springbootinit.model.dto.postfavour;

import com.mmm.springbootinit.model.dto.post.PostQueryRequest;
import com.mmm.springbootinit.common.PageRequest;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *@Date：2024/3/2
 *@Author：mmm
 *@return：
 * 帖子收藏查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PostFavourQueryRequest extends PageRequest implements Serializable {

    /**
     * 帖子查询请求
     */
    private PostQueryRequest postQueryRequest;

    /**
     * 用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}