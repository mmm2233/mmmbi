package com.mmm.springbootinit.service;

import com.mmm.springbootinit.model.entity.Ordersinfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface OrdersinfoService extends IService<Ordersinfo> {

    String getPayNo(long orderId, long userId);
}
