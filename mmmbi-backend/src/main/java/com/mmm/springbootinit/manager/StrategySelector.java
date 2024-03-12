package com.mmm.springbootinit.manager;

import com.mmm.springbootinit.model.enums.GenChartStrategyEnum;
import com.mmm.springbootinit.service.GenChartStrategy;
import com.mmm.springbootinit.utils.ServerLoadInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.manager
 * @Project：mmmbi-backend
 * @name：StrategySelector
 * @Date：2024/3/12 11:55
 * @Filename：StrategySelector
 */
@Component
public class StrategySelector {

    /**
     * Spring会自动将strategy接口的实现类注入到这个Map中，key为bean id,value值则为对应的策略实现类
     */
    @Resource
    Map<String, GenChartStrategy> strategyMap;

    /**
     * 选择对应的生成图表执行策略
     *
     * @param info 服务器当前负载信息
     * @return {@link GenChartStrategy}
     */
    public GenChartStrategy selectStrategy(ServerLoadInfo info) {
        if (info.isVeryHighLoad()) {
            return strategyMap.get(GenChartStrategyEnum.GEN_REJECT.getValue());
        } else if (info.isHighLoad()) {
            return strategyMap.get(GenChartStrategyEnum.GEN_MQ.getValue());
        } else if (info.isMediumLoad()) {
            return strategyMap.get(GenChartStrategyEnum.GEN_THREAD_POOL.getValue());
        } else {
            return strategyMap.get(GenChartStrategyEnum.GEN_SYNC.getValue());
        }
    }
}
