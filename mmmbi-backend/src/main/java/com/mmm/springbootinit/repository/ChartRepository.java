package com.mmm.springbootinit.repository;

import com.mmm.springbootinit.model.document.ChartGen;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.repository
 * @Project：mmmbi-backend
 * @name：ChartRepository
 * @Date：2024/3/12 11:57
 * @Filename：ChartRepository
 */
@Component
public interface ChartRepository extends MongoRepository<ChartGen,String> {

    @Query("{'userId': ?0}")
    List<ChartGen> findAllByUserId(long userId, Pageable pageable);


    long deleteAllByChartId(long chartId);

    @Query("{'chartId': ?0}")
    List<ChartGen> findAllByChartId(long chartId);
}
