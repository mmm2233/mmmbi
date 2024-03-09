package com.mmm.springbootinit.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmm.springbootinit.annotation.AuthCheck;
import com.mmm.springbootinit.common.BaseResponse;
import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.common.ResultUtils;
import com.mmm.springbootinit.constant.CommonConstant;
import com.mmm.springbootinit.constant.UserConstant;
import com.mmm.springbootinit.exception.BusinessException;
import com.mmm.springbootinit.exception.ThrowUtils;
import com.mmm.springbootinit.model.dto.ordeersinfo.OrdersInfoQueryRequest;
import com.mmm.springbootinit.model.entity.Ordersinfo;
import com.mmm.springbootinit.model.entity.User;
import com.mmm.springbootinit.model.enums.UserRoleEnum;
import com.mmm.springbootinit.service.UserService;
import com.mmm.springbootinit.service.impl.OrdersinfoServiceImpl;
import com.mmm.springbootinit.utils.SqlUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.controller
 * @Project：mmmbi-backend
 * @name：AliPayInfoController
 * @Date：2024/3/9 14:47
 * @Filename：AliPayInfoController
 */
@RestController
@RequestMapping("/payInfo")
public class OrdersInfoController {

    @Resource
    private OrdersinfoServiceImpl ordersinfoService;

    @Resource
    private UserService userService;

    /*
        分页获取用户订单
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Ordersinfo>> listOrdersInfoByPage(@RequestBody OrdersInfoQueryRequest ordersInfoQueryRequest,HttpServletRequest httpServletRequest){
        long current = ordersInfoQueryRequest.getCurrent();
        long size = ordersInfoQueryRequest.getPageSize();
        Page<Ordersinfo> orderPage = ordersinfoService.page(new Page<>(current, size),
                getOrdersInfoQueryWrapper(ordersInfoQueryRequest));
        return ResultUtils.success(orderPage);
    }

    @PostMapping("/get/id")
    //todo 管理员可以看所有人的订单，用户看自己的
    public BaseResponse<Ordersinfo> MyPayInfoByPage(@RequestBody OrdersInfoQueryRequest ordersInfoQueryRequest,
                                                              HttpServletRequest request) {
        if (ordersInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Ordersinfo one = ordersinfoService.getOne(getOrdersInfoQueryWrapper(ordersInfoQueryRequest));
        ThrowUtils.throwIf(one.getUserId()!= loginUser.getId()
                && loginUser.getUserRole()!= UserConstant.ADMIN_ROLE,ErrorCode.NO_AUTH_ERROR);
        return  ResultUtils.success(one);
    }


    /*
     * 分页获取当前用户的订单
     */
    @PostMapping("/list/my/page")
    public BaseResponse<Page<Ordersinfo>> listMyPayInfoByPage(@RequestBody OrdersInfoQueryRequest ordersInfoQueryRequest,
                                                              HttpServletRequest request) {
        if (ordersInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        ordersInfoQueryRequest.setUserId(loginUser.getId());
        long current = ordersInfoQueryRequest.getCurrent();
        long size = ordersInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Ordersinfo> chartPage = ordersinfoService.page(new Page<>(current, size),
                getOrdersInfoQueryWrapper(ordersInfoQueryRequest));
        return ResultUtils.success(chartPage);
    }

    private QueryWrapper<Ordersinfo> getOrdersInfoQueryWrapper(OrdersInfoQueryRequest ordersInfoQueryRequest) {

        Long id = ordersInfoQueryRequest.getId();
        Long userId = ordersInfoQueryRequest.getUserId();
        Long orderId = ordersInfoQueryRequest.getOrderId();
        String sortField = ordersInfoQueryRequest.getSortField();
        String sortOrder = ordersInfoQueryRequest.getSortOrder();

        QueryWrapper<Ordersinfo> queryWrapper = new QueryWrapper<>();
        if (ordersInfoQueryRequest == null) {
            return queryWrapper;
        }
        // 根据前端传来条件进行拼接查询条件
        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_DESC),
                sortField);
        return queryWrapper;
    }
}
