package com.mmm.springbootinit.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmm.springbootinit.common.BaseResponse;
import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.common.PageRequest;
import com.mmm.springbootinit.common.ResultUtils;
import com.mmm.springbootinit.exception.BusinessException;
import com.mmm.springbootinit.model.dto.points.CzUpdateRequest;
import com.mmm.springbootinit.model.entity.PointChanges;
import com.mmm.springbootinit.model.entity.Points;
import com.mmm.springbootinit.model.entity.User;
import com.mmm.springbootinit.model.enums.PointChangeEnum;
import com.mmm.springbootinit.service.PointChangesService;
import com.mmm.springbootinit.service.PointsService;
import com.mmm.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.controller
 * @Project：mmmbi-backend
 * @name：PointController
 * @Date：2024/3/12 13:51
 * @Filename：PointController
 */
@RestController
@Slf4j
@RequestMapping("/point")
public class PointController {
    @Resource
    PointsService pointService;

    @Resource
    PointChangesService pointChangeService;

    @Resource
    UserService userService;

    @GetMapping("/get/daily")
    public BaseResponse getDailyLoginPoint(HttpServletRequest httpServletRequest) {
        User user = userService.getLoginUser(httpServletRequest);

        Long userId = user.getId();
        boolean res = pointService.getDailyLoginPoint(userId);
        if (res) {
            return ResultUtils.success("领取成功!");
        } else {
            return ResultUtils.success("领取失败,请勿重复领取!");
        }
    }

    @PostMapping("/log")
    public BaseResponse<Page<PointChanges>> getPointChangeRecords(@Validated @RequestBody PageRequest pageRequest,HttpServletRequest httpServletRequest) {
        User user = userService.getLoginUser(httpServletRequest);

        Long userId = user.getId();
        Page<PointChanges> pointEntityPage = pointChangeService.query().eq("userId", userId)
                .orderByDesc("createTime")
                .page(new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize()));
        return ResultUtils.success(pointEntityPage);
    }

    @GetMapping("/get/user")
    public BaseResponse<Points> getUserPoint(Long userId, HttpServletRequest request) {
        if (userId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<Points> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",loginUser.getId());
        Points points = pointService.getOne(queryWrapper);
        return ResultUtils.success(points);
    }

    @PostMapping("/cz/user")
    public boolean czPoint(CzUpdateRequest czUpdateRequest, HttpServletRequest request) {
        if (czUpdateRequest.getUserId() == null || czUpdateRequest.getAmount() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PointChangeEnum czAlipayAdd = PointChangeEnum.CZ_ALIPAY_ADD;
        czAlipayAdd.setChangeAmount(czUpdateRequest.getAmount());
        return pointService.czPoint(czUpdateRequest,request);
    }
}
