package com.mmm.springbootinit.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.constant.CreditConstant;
import com.mmm.springbootinit.exception.BusinessException;
import com.mmm.springbootinit.exception.ThrowUtils;
import com.mmm.springbootinit.model.entity.Credit;
import com.mmm.springbootinit.service.CreditService;
import com.mmm.springbootinit.mapper.CreditMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 *
 */
@Service
public class CreditServiceImpl extends ServiceImpl<CreditMapper, Credit>
    implements CreditService{

    @Override
    public Long getCreditTotal(Long userId) {
        if (userId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 可能查询字段组合有点多的话可以写个方法区单独拼接
        QueryWrapper<Credit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        Credit credit = this.getOne(queryWrapper);
        ThrowUtils.throwIf(credit == null,ErrorCode.NOT_FOUND_ERROR);
        return credit.getCreditTotal();
    }

    @Override
    // 验证线程安全性
    public Boolean signUser(Long userId) {
        if (userId == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        QueryWrapper<Credit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        // todo 引入redis加锁，防止超卖
        // 使用同步机制：使用关键字 synchronized 或者 Lock 接口来保护关键代码块或方法，以实现线程安全。确保只有一个线程能够访问和修改共享变量。
        // 使用乐观锁（Optimistic Locking）：通过为 Credit 对象增加一个版本号字段，并在更新时比较版本号，可以避免并发更新冲突的问题。
        // 使用事务：将整个代码段放在一个事务中，可以保证原子性和一致性，并避免并发修改导致的数据不一致性问题
        synchronized (userId.toString().intern()){
            Credit credit = this.getOne(queryWrapper);
            ThrowUtils.throwIf(credit == null, ErrorCode.NOT_FOUND_ERROR);
            Date checkTime = credit.getCheckTime();
            //判断今天是否已经签过
            if ( checkTime != null && DateUtil.isSameDay(checkTime, new DateTime())) {
                return false;
            }

            Long creditTotal = credit.getCreditTotal() + CreditConstant.CREDIT_DAILY;
            credit.setCreditTotal(creditTotal);
            credit.setCheckTime(new Date());
            return this.updateById(credit);
        }
    }

    @Override
    @Transactional
    public Boolean consumeCredits(Long userId, Long credits) {
        if (userId == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        QueryWrapper<Credit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        //synchronized (userId.toString().intern()) {
            Credit credit = this.getOne(queryWrapper);
            ThrowUtils.throwIf(credit == null, ErrorCode.NOT_FOUND_ERROR);
            Long creditTotal = credit.getCreditTotal();
            if (credits < 0 && creditTotal > credits) {
                //积分不足
                return false;
            }
            creditTotal = creditTotal + credits;
            credit.setCreditTotal(creditTotal);
            return this.updateById(credit);
        //}
    }

}




