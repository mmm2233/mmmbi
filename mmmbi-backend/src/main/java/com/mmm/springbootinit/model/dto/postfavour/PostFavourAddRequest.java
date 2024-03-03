package com.mmm.springbootinit.model.dto.postfavour;

import java.io.Serializable;
import lombok.Data;

/**
 *@Date：2024/3/2
 *@Author：mmm
 *@return：
 * 帖子收藏 / 取消收藏请求
 */
@Data
public class PostFavourAddRequest implements Serializable {

    /**
     * 帖子 id
     */
    private Long postId;

    private static final long serialVersionUID = 1L;
}