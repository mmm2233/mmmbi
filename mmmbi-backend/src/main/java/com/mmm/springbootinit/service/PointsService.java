package com.mmm.springbootinit.service;

import com.mmm.springbootinit.model.entity.Points;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mmm.springbootinit.model.enums.PointChangeEnum;

/**
 *
 */
public interface PointsService extends IService<Points> {
    /**
     * 核对用户积分是否足够并扣除
     *
     * @param userId          用户id
     * @param pointChangeEnum 点变换枚举
     * @return boolean true:计分足够 false:积分不足
     */
    boolean checkAndDeduct(long userId , PointChangeEnum pointChangeEnum);

    /**
     * 获取每日登录积分
     *
     * @param userId 用户id
     * @return boolean
     */
    boolean getDailyLoginPoint(Long userId);

    /**
     * 今天是否已经领取
     *
     * @param userId 用户id
     * @return boolean
     */
    boolean isAlreadyGetted(Long userId);

    /**
     * 补偿积分
     *
     * @param userId          用户id
     * @param pointChangeEnum 积分变换枚举
     * @return boolean
     */
    boolean compensatePoint(Long userId, PointChangeEnum pointChangeEnum);


    /**
     * 发送补偿信息到MQ
     *
     * @param userId          用户id
     * @param pointChangeEnum 积分变换枚举
     */
    void sendCompensateMessage(Long userId, PointChangeEnum pointChangeEnum);
}
