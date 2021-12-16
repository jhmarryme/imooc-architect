package com.example.order.controller;

import com.example.controller.BaseController;

import com.example.enums.OrderStatusEnum;
import com.example.enums.PayMethod;
import com.example.order.pojo.OrderStatus;
import com.example.order.pojo.bo.PlaceOrderBO;
import com.example.order.pojo.bo.SubmitOrderBO;
import com.example.order.pojo.vo.MerchantOrdersVO;
import com.example.order.pojo.vo.OrderVO;
import com.example.order.service.OrderService;
import com.example.pojo.CommonResult;
import com.example.pojo.ShopcartBO;
import com.example.utils.CookieUtils;
import com.example.utils.JsonUtils;
import com.example.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Api(value = "订单相关", tags = {"订单相关的api接口"})
@RequestMapping("orders")
@RestController
public class OrdersController extends BaseController {

    final static Logger logger = LoggerFactory.getLogger(OrdersController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisOperator redisOperator;

    @Autowired
    private RedissonClient redisson;

    @RequestMapping(path = "/getOrderToken", method = {RequestMethod.GET, RequestMethod.POST})
    public CommonResult getOrderToken(HttpSession session) {
        String token = UUID.randomUUID().toString();
        redisOperator.set("ORDER_TOKEN_" + session.getId(), token, 600);
        return CommonResult.ok(token);
    }

    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
    public CommonResult create(
            @RequestBody SubmitOrderBO submitOrderBO,
            HttpServletRequest request,
            HttpServletResponse response) {

        // ==================== 幂等性改造 start ====================
        String orderTokenKey = "ORDER_TOKEN_" + request.getSession().getId();
        String rLockKey = "ORDER_LOCK_" + request.getSession().getId();
        RLock rLock = redisson.getLock(rLockKey);
        rLock.lock(5, TimeUnit.SECONDS);
        try {
            String token = submitOrderBO.getToken();
            String tokenInStore = redisOperator.get(orderTokenKey);
            if (StringUtils.isBlank(tokenInStore)) {
                throw new RuntimeException("orderToken不存在");
            }
            if (!tokenInStore.equals(token)) {
                throw new RuntimeException("orderToken不正确");
            }
            redisOperator.del(orderTokenKey);
        } finally {
            try {
                rLock.unlock();
            } catch (Exception ignored) {

            }
        }
        // ==================== 幂等性改造 end ====================
        if (submitOrderBO.getPayMethod() != PayMethod.WEIXIN.type
                && submitOrderBO.getPayMethod() != PayMethod.ALIPAY.type) {
            return CommonResult.errorMsg("支付方式不支持！");
        }

//        System.out.println(submitOrderBO.toString());

        String shopcartJson = redisOperator.get(FOODIE_SHOPCART + ":" + submitOrderBO.getUserId());
        if (StringUtils.isBlank(shopcartJson)) {
            return CommonResult.errorMsg("购物数据不正确");
        }

        List<ShopcartBO> shopcartList = JsonUtils.jsonToList(shopcartJson, ShopcartBO.class);
        PlaceOrderBO placeOrderBO = new PlaceOrderBO(submitOrderBO, shopcartList);
        // 1. 创建订单
        OrderVO orderVO = orderService.createOrder(placeOrderBO);
        String orderId = orderVO.getOrderId();

        // 2. 创建订单以后，移除购物车中已结算（已提交）的商品
        /**
         * 1001
         * 2002 -> 用户购买
         * 3003 -> 用户购买
         * 4004
         */
        // 清理覆盖现有的redis汇总的购物数据
        shopcartList.removeAll(orderVO.getToBeRemovedShopcatdList());
        redisOperator.set(FOODIE_SHOPCART + ":" + submitOrderBO.getUserId(), JsonUtils.objectToJson(shopcartList));
        // 整合redis之后，完善购物车中的已结算商品清除，并且同步到前端的cookie
        CookieUtils.setCookie(request, response, FOODIE_SHOPCART, JsonUtils.objectToJson(shopcartList), true);

        // 3. 向支付中心发送当前订单，用于保存支付中心的订单数据
        MerchantOrdersVO merchantOrdersVO = orderVO.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(payReturnUrl);

        // 为了方便测试购买，所以所有的支付金额都统一改为1分钱
        merchantOrdersVO.setAmount(1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("imoocUserId", "example");
        headers.add("password", "example");

        HttpEntity<MerchantOrdersVO> entity =
                new HttpEntity<>(merchantOrdersVO, headers);

        ResponseEntity<CommonResult> responseEntity =
                restTemplate.postForEntity(paymentUrl,
                        entity,
                        CommonResult.class);
        CommonResult paymentResult = responseEntity.getBody();
        if (paymentResult.getStatus() != 200) {
            logger.error("发送错误：{}", paymentResult.getMsg());
            return CommonResult.errorMsg("支付中心订单创建失败，请联系管理员！");
        }

        return CommonResult.ok(orderId);
    }

    @PostMapping("notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(String merchantOrderId) {
        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }

    @PostMapping("getPaidOrderInfo")
    public CommonResult getPaidOrderInfo(String orderId) {

        OrderStatus orderStatus = orderService.queryOrderStatusInfo(orderId);
        return CommonResult.ok(orderStatus);
    }
}
