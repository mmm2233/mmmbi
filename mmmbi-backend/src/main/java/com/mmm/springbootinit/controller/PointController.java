package com.mmm.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmm.springbootinit.common.BaseResponse;
import com.mmm.springbootinit.common.PageRequest;
import com.mmm.springbootinit.common.ResultUtils;
import com.mmm.springbootinit.model.entity.PointChanges;
import com.mmm.springbootinit.model.entity.User;
import com.mmm.springbootinit.service.PointChangesService;
import com.mmm.springbootinit.service.PointsService;
import com.mmm.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
public class PointController {
    @Resource
    PointsService pointService;

    @Resource
    PointChangesService pointChangeService;

    @Resource
    UserService userService;

    @GetMapping("/get/daily/point")
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

    @PostMapping("/point/log")
    public BaseResponse<Page<PointChanges>> getPointChangeRecords(@Validated @RequestBody PageRequest pageRequest,HttpServletRequest httpServletRequest) {
        User user = userService.getLoginUser(httpServletRequest);

        Long userId = user.getId();
        Page<PointChanges> pointEntityPage = pointChangeService.query().eq("userId", userId)
                .orderByDesc("createTime")
                .page(new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize()));
        return ResultUtils.success(pointEntityPage);
    }
}
