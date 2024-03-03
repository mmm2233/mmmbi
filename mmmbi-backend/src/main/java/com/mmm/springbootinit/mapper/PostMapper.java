package com.mmm.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mmm.springbootinit.model.entity.Post;
import java.util.Date;
import java.util.List;

/**
 *@Date：2024/3/2
 *@Author：mmm
 *@return：
 * 帖子数据库操作
 */
public interface PostMapper extends BaseMapper<Post> {

    /**
     * 查询帖子列表（包括已被删除的数据）
     */
    List<Post> listPostWithDelete(Date minUpdateTime);

}




