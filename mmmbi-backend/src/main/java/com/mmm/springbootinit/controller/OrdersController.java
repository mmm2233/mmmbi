package com.mmm.springbootinit.controller;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmm.springbootinit.common.BaseResponse;
import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.common.ResultUtils;
import com.mmm.springbootinit.exception.BusinessException;
import com.mmm.springbootinit.model.entity.Ordersinfo;
import com.mmm.springbootinit.model.entity.User;
import com.mmm.springbootinit.model.vo.OrdersInfoVO;
import com.mmm.springbootinit.service.OrdersinfoService;
import com.mmm.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.controller
 * @Project：mmmbi-backend
 * @name：AliPayController
 * @Date：2024/3/9 14:39
 * @Filename：AliPayController
 */
@Controller
@RequestMapping("/alipay")
@Slf4j
public class OrdersController {

    @Resource
    private OrdersinfoService ordersinfoService;

    @Resource
    private UserService userService;

    public static String URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    public static String CHARSET = "UTF-8";
    public static String SIGNTYPE = "RSA2";

    @Value("${pay.alipay.APP_ID}")
    String APP_ID;
    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    String APP_PRIVATE_KEY;

    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    String ALIPAY_PUBLIC_KEY;

    /*
        支付接口
     */

    @GetMapping("/pay")
    public String pay() throws AlipayApiException, IOException {
        AlipayClient alipayClient = new DefaultAlipayClient(URL,APP_ID,APP_PRIVATE_KEY,"json","GBK",
                ALIPAY_PUBLIC_KEY,"RSA2");
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //异步接收地址，仅支持http/https，公网可访问
        //request.setNotifyUrl("");
        //同步跳转地址，仅支持http/https
        //request.setReturnUrl("");
        /******必传参数******/
        JSONObject bizContent = new JSONObject();
        //商户订单号，商家自定义，保持唯一性
        bizContent.put("out_trade_no", "20210817010111005");
        //支付金额，最小值0.01元
        bizContent.put("total_amount", 1000);
        //订单标题，不可使用特殊符号
        bizContent.put("subject", "测试商品");
        //电脑网站支付场景固定传值FAST_INSTANT_TRADE_PAY
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

        request.setBizContent(bizContent.toString());
        //AlipayTradePagePayResponse response = alipayClient.pageExecute(request,"POST");
        // 如果需要返回GET请求，请使用
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request,"GET");
        String pageRedirectionData = response.getBody();
        System.out.println(pageRedirectionData);

        return pageRedirectionData;

//        if(response.isSuccess()){
//            System.out.println("调用成功");
//        } else {
//            System.out.println("调用失败");
//        }
    }

    /**
     * 生成二维码
     *
     * @param orderId
     * @param request
     * @return
     */
    @PostMapping("/payCode")
    @ResponseBody
    public BaseResponse<OrdersInfoVO> payCode(long orderId, HttpServletRequest request) {
        if (orderId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        String alipayAccountNo = ordersinfoService.getPayNo(orderId, loginUser.getId());
        //String url = String.format("http://xxxxxx:8103/api/alipay/pay?alipayAccountNo=%s", alipayAccountNo);
        String url = String.format("http://192.168.11.219:8108/api/alipay/pay?alipayAccountNo=%s", alipayAccountNo);
        String generateQrCode = QrCodeUtil.generateAsBase64(url, new QrConfig(400, 400), "png");
        OrdersInfoVO ordersinfo = new OrdersInfoVO();
        ordersinfo.setAlipayAccountNo(String.valueOf(alipayAccountNo));

        ordersinfo.setQrCode(generateQrCode);
        ordersinfo.setOrderId(orderId);
        return ResultUtils.success(ordersinfo);
    }

    /**
     * 查询交易结果
     *
     * @throws AlipayApiException
     */
    @Transactional
    @PostMapping("/tradeQuery")
    public void tradeQuery(String alipayAccountNo) throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient(URL, APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, SIGNTYPE);
        Ordersinfo alipayInfo = getTotalAmount(alipayAccountNo);
        Long orderId = alipayInfo.getOrderId();

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", alipayAccountNo);

//        request.setBizContent(bizContent.toString());
//        AlipayTradeQueryResponse response = alipayClient.execute(request);
//        if (!response.isSuccess()) {
//            log.error("查询交易结果失败");
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "调用失败");
//        }
//        //获取支付结果
//        String resultJson = response.getBody();
//        //转map
//        Map resultMap = JSON.parseObject(resultJson, Map.class);
//        Map alipay_trade_query_response = (Map) resultMap.get("alipay_trade_query_response");
//        String trade_no = (String) alipay_trade_query_response.get("trade_no");
//        alipayInfo.setPayStatus(Integer.valueOf(PayOrderEnum.COMPLETE.getValue()));
//        alipayInfo.setAlipayId(trade_no);
//        boolean updateComplete = alipayInfoService.updateById(alipayInfo);
//        if (!updateComplete) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        AiFrequencyOrder order = getOrder(orderId);
//        order.setOrderStatus(Integer.valueOf(PayOrderEnum.COMPLETE.getValue()));
//        aiFrequencyOrderService.updateById(order);
//        // 获取充值次数
//        Long total = order.getPurchaseQuantity();
//        Long userId = order.getUserId();
//        AiFrequency aiFrequency = getHartFrequency(userId);
//
//        if (aiFrequency == null) {
//            AiFrequency frequency = new AiFrequency();
//            frequency.setUserId(userId);
//            frequency.setTotalFrequency(Integer.valueOf(PayOrderEnum.WAIT_PAY.getValue()));
//            frequency.setRemainFrequency(Math.toIntExact(total));
//            boolean save = aiFrequencyService.save(frequency);
//            if (!save) {
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "支付成功回调发生错误");
//            }
//        }
//        Integer remainFrequency = aiFrequency.getRemainFrequency();
//        int i = Math.toIntExact(total);
//        aiFrequency.setRemainFrequency(remainFrequency + i);
//        boolean updateFrequency = aiFrequencyService.updateById(aiFrequency);
//        if (!updateFrequency) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "支付成功回调发生错误");
//        }
//        log.info("调用成功，结果：" + response.getBody());
//        //return ResultUtils.success(resultJson);
    }

    /**
     * 请求支付宝查询支付结果
     *
     * @param alipayAccountNo 支付交易号
     * @return 支付结果
     */
    @PostMapping("/query/payNo")
    @ResponseBody
    public void queryPayResultFromAlipay(String alipayAccountNo) {
        AlipayClient alipayClient = new DefaultAlipayClient(URL, APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, SIGNTYPE);
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        try {
            bizContent.put("out_trade_no", alipayAccountNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // bizContent.put("trade_no", "2014112611001004680073956707");
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
        //获取支付结果
        String resultJson = response.getBody();
        //转map
        Map resultMap = JSON.parseObject(resultJson, Map.class);
        Map alipay_trade_query_response = (Map) resultMap.get("alipay_trade_query_response");
        //支付结果
        String trade_status = (String) alipay_trade_query_response.get("trade_status");
        String total_amount = (String) alipay_trade_query_response.get("total_amount");
        String trade_no = (String) alipay_trade_query_response.get("trade_no");
    }

    /**
     * 获取支付宝流水账号
     *
     * @param alipayAccountNo
     * @return
     */
    public Ordersinfo getTotalAmount(String alipayAccountNo) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("alipayAccountNo", alipayAccountNo);
        Ordersinfo aliPayOne = ordersinfoService.getOne(wrapper);
        if (aliPayOne == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "没有这个记录");
        }
        return aliPayOne;
    }
}