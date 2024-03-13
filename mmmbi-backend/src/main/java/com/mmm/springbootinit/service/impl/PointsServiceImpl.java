package com.mmm.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmm.springbootinit.common.BaseResponse;
import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.common.ResultUtils;
import com.mmm.springbootinit.exception.BusinessException;
import com.mmm.springbootinit.exception.ThrowUtils;
import com.mmm.springbootinit.model.dto.points.CzUpdateRequest;
import com.mmm.springbootinit.model.entity.PointChanges;
import com.mmm.springbootinit.model.entity.Points;
import com.mmm.springbootinit.model.entity.User;
import com.mmm.springbootinit.model.enums.PointChangeEnum;
import com.mmm.springbootinit.service.PointChangesService;
import com.mmm.springbootinit.service.PointsService;
import com.mmm.springbootinit.mapper.PointsMapper;
import com.mmm.springbootinit.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Date;

/**
 *
 */
@Service
public class PointsServiceImpl extends ServiceImpl<PointsMapper, Points>
    implements PointsService{

    @Resource
    UserService userService;

    @Resource
    PointChangesService pointChangeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean checkAndDeduct(long userId, PointChangeEnum pointChangeEnum) {
        return operatePointAndSave(userId, pointChangeEnum);
    }

    @Override
    public boolean getDailyLoginPoint(Long userId) {
        // 校验是否已经领取过
        if (isAlreadyGetted(userId)) return false;
        PointChangeEnum pointChangeEnum = PointChangeEnum.DAILY_LOGIN_ADD;
        // 查询积分
        return operatePointAndSave(userId, pointChangeEnum);
    }

    @Override
    public boolean isAlreadyGetted(Long userId) {
        // 获取今天的日期
        LocalDate today = LocalDate.now();
        PointChanges point = pointChangeService.getOne(
                new QueryWrapper<PointChanges>()
                        .eq("userId", userId)
                        .eq("reason", PointChangeEnum.DAILY_LOGIN_ADD.getReason())
                        .ge("createTime", today.atStartOfDay())  // 大于等于今天的开始时间
                        .lt("createTime", today.plusDays(1).atStartOfDay())  // 小于今天的开始时间
        );
        return point != null;
    }

    @Override
    public boolean compensatePoint(Long userId, PointChangeEnum pointChangeEnum) {
        return operatePointAndSave(userId, pointChangeEnum);
    }

    @Override
    public BaseResponse<Points> getUserPoint(Long userId, HttpServletRequest request) {
        if (userId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<Points> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",loginUser.getId());
        Points points = getOne(queryWrapper);
        return ResultUtils.success(points);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean czPoint(CzUpdateRequest czUpdateRequest, HttpServletRequest request) {
        if (czUpdateRequest.getUserId() == null || czUpdateRequest.getAmount() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PointChangeEnum czAlipayAdd = PointChangeEnum.CZ_ALIPAY_ADD;
        czAlipayAdd.setChangeAmount(czUpdateRequest.getAmount());
        return operatePointAndSave(czUpdateRequest.getUserId(), czAlipayAdd);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendCompensateMessage(Long userId, PointChangeEnum pointChangeEnum) {
        operatePointAndSave(userId,pointChangeEnum);
        // 不用使用消息队列
//        rabbitTemplate.convertAndSend(BiMqConstant.BI_EXCHANGE_NAME, BiMqConstant.COMPENSATE_POINT_ROUTING_KEY,
//                new JSONObject().set("userId", userId).set("pointChangeEnum", pointChangeEnum));
    }

    /**
     * 操作点并保存记录
     *
     * @param userId          用户id
     * @param pointChangeEnum 积分change枚举
     * @return boolean true表示操作成功, false表示操作失败
     */
    private boolean operatePointAndSave(long userId, PointChangeEnum pointChangeEnum) {
        // 查询积分
        Points point = getOne(new QueryWrapper<Points>().eq("userId", userId));
        if (point == null) {
            point = new Points();
            point.setUserId(userId);
            point.setTotalPoints(0L);
            point.setRemainingPoints(0L);
            // 获取索引值作为积分状态默认有效
            point.setUpdateTime(new Date());
            point.setStatus((byte)Points.Status.VALID.ordinal());
            ThrowUtils.throwIf(!save(point), ErrorCode.SYSTEM_ERROR, "保存用户积分信息失败!");
        }
        // 操作积分
        if (pointChangeEnum.getChangeType() == PointChangeEnum.ChangeType.INCREASE) {
            point.setTotalPoints(point.getTotalPoints() + pointChangeEnum.getChangeAmount());
            point.setRemainingPoints(point.getRemainingPoints() + pointChangeEnum.getChangeAmount());
        } else if (pointChangeEnum.getChangeType() == PointChangeEnum.ChangeType.DECREASE) {
            // 扣除
            if (point.getRemainingPoints() < pointChangeEnum.getChangeAmount()) {
                return false;
            }
            point.setRemainingPoints(point.getRemainingPoints() - pointChangeEnum.getChangeAmount());
        }
        // 写入相关积分记录
        PointChanges pointChange = new PointChanges(pointChangeEnum, userId);
        pointChange.setNewPoints(point.getRemainingPoints());
        // 保存
        pointChangeService.save(pointChange);
        updateById(point);
        return true;
    }
}




