package com.mmm.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmm.springbootinit.model.entity.Orders;
import com.mmm.springbootinit.service.OrdersService;
import com.mmm.springbootinit.mapper.OrdersMapper;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
    implements OrdersService{

}




