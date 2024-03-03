package com.mmm.springbootinit.esdao;

import com.mmm.springbootinit.model.dto.post.PostEsDTO;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
*@Date：2024/3/2
*@Author：mmm
*@return：
* 帖子 ES 操作
*/
public interface PostEsDao extends ElasticsearchRepository<PostEsDTO, Long> {

    List<PostEsDTO> findByUserId(Long userId);
}