package com.mmm.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mmm.springbootinit.model.entity.Credit;

/**
 *
 */
public interface CreditService extends IService<Credit> {
    public Long getCreditTotal(Long userId);

    // 每日签到接口
    public Boolean signUser(Long userId);

    public Boolean consumeCredits(Long userId, Long credits);
}
