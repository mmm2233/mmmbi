package com.mmm.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.exception.BusinessException;
import com.mmm.springbootinit.model.entity.Ordersinfo;
import com.mmm.springbootinit.service.OrdersinfoService;
import com.mmm.springbootinit.mapper.OrdersinfoMapper;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class OrdersinfoServiceImpl extends ServiceImpl<OrdersinfoMapper, Ordersinfo>
    implements OrdersinfoService{

    @Override
    public String getPayNo(long orderId, long userId) {
        Ordersinfo ordersinfo = this.getById(orderId);
        Integer orderStatus = ordersinfo.getPayStatus();
//        if (orderStatus == 1) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "订单已支付");
//        }
//        if (orderStatus == 2) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "订单已过期，请重新生成订单");
//        }
        return ordersinfo.getAlipayAccountNo();
    }

}




