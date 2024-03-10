package com.mmm.springbootinit.constant;

/**
 * 支付订单常量
 * @Author：mmm
 * @Package：com.mmm.springbootinit.constant
 * @Project：mmmbi-backend
 * @name：OrdersConstant
 * @Date：2024/3/9 13:04
 * @Filename：OrdersConstant
 */
public interface OrdersConstant {
    /**
     * 回调地址(本地部署时需要内网穿透)
     */
    String NOTIFYURL = " http://6mpwva.natappfree.cc/api/alipay/notify";

    /**
     * 未支付
     */
    String UNPAID = "unpaid";

    //  region 权限

    /**
     * 支付中
     */
    String PAYING = "paying";

    /**
     * 成功
     */
    String SUCCEED = "succeed";

    /**
     * 失败
     */
    String FAILED = "failed";
}
