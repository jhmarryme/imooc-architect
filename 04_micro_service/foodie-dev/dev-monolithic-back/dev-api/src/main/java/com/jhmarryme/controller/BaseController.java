package com.jhmarryme.controller;

import com.jhmarryme.pojo.Orders;
import com.jhmarryme.pojo.Users;
import com.jhmarryme.pojo.vo.UsersVO;
import com.jhmarryme.service.center.MyOrdersService;
import com.jhmarryme.utils.CommonResult;
import com.jhmarryme.utils.RedisOperator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.util.UUID;

@Controller
public class BaseController {

    public static final String FOODIE_SHOPCART = "shopcart";

    public static final Integer COMMON_PAGE_SIZE = 10;
    public static final Integer PAGE_SIZE = 20;

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    // 支付中心的调用地址
    String paymentUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";		// produce

    // 微信支付成功 -> 支付中心 -> 天天吃货平台
    //                       |-> 回调通知的url
//    String payReturnUrl = "http://api.z.mukewang.com/foodie-dev-api/orders/notifyMerchantOrderPaid";
    String payReturnUrl = "http://api.architect.study.jhmarryme.top/foodie-dev-api/orders/notifyMerchantOrderPaid";
//    String payReturnUrl = "http://rarf8c.natappfree.cc/orders/notifyMerchantOrderPaid";

    // 用户上传头像的位置
    public static final String IMAGE_USER_FACE_LOCATION = File.separator + "workspaces" +
                                                            File.separator + "images" +
                                                            File.separator + "foodie" +
                                                            File.separator + "faces";
//    public static final String IMAGE_USER_FACE_LOCATION = "/workspaces/images/foodie/faces";


    @Autowired
    public MyOrdersService myOrdersService;

    @Autowired
    private RedisOperator redisOperator;

    /**
     * 用于验证用户和订单是否有关联关系，避免非法用户调用
     *
     * @author Jiahao Wang
     * @date 2021/4/5 1:00 下午
     * @param userId userId
     * @param orderId orderId
     * @return com.jhmarryme.utils.CommonResult
     */
    public CommonResult checkUserOrder(String userId, String orderId) {
        Orders order = myOrdersService.queryMyOrder(userId, orderId);
        if (order == null) {
            return CommonResult.errorMsg("订单不存在！");
        }
        return CommonResult.ok(order);
    }

    public UsersVO conventUsersVO(Users user) {
        // 实现用户的redis会话
        String uniqueToken = UUID.randomUUID().toString().trim();
        redisOperator.set(REDIS_USER_TOKEN + ":" + user.getId(),
                uniqueToken);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        return usersVO;
    }
}
