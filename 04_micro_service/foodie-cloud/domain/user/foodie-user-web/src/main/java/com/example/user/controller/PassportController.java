package com.example.user.controller;

import com.example.auth.service.AuthService;
import com.example.auth.service.entity.Account;
import com.example.auth.service.entity.AuthResponse;
import com.example.auth.service.entity.AuthResponseCode;
import com.example.controller.BaseController;
import com.example.pojo.CommonResult;
import com.example.pojo.ShopcartBO;
import com.example.user.pojo.Users;
import com.example.user.pojo.bo.UserBO;
import com.example.user.pojo.vo.UserVO;
import com.example.user.resource.UserApplicationProperties;
import com.example.user.service.UserService;
import com.example.utils.CookieUtils;
import com.example.utils.JsonUtils;
import com.example.utils.MD5Utils;
import com.example.utils.RedisOperator;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Api(value = "注册登录", tags = {"用于注册登录的相关接口"})
@RestController
@RequestMapping("passport")
@Slf4j
public class PassportController extends BaseController {

    /** token 使用的头 */
    private static final String AUTH = "Authorization";

    /** 存放refresh token的头 */
    private static final String REFRESH_TOKEN_HEADER = "refresh-token";

    /** 存放用户的头 */
    private static final String USERNAME = "imooc-user-name";

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

    @Autowired
    private UserApplicationProperties userApplicationProperties;

    @Autowired
    private AuthService authService;

    @ApiOperation(value = "用户名是否存在", notes = "用户名是否存在", httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    @HystrixCommand(
            // 全局唯一的标识服务，默认是方法签名
            commandKey = "loginFail",
            // 全局服务分组，用于组织仪表盘，统计信息分组
            // 如果不指定的话，会默认指定一个值，以类名作为默认值
            groupKey = "password",
            // 指向当前类的 loginFail 方法，签名需要是 public 或则 private
            fallbackMethod = "loginFail",
            // 在列表中声明的异常类型不会触发降级
            ignoreExceptions = {IllegalArgumentException.class},
            // 线程有关属性配置
            // 线程组：多个服务可以共用一个线程组
            threadPoolKey = "threadPoolA",
            threadPoolProperties = {
                    // 有很多属性可配置，配置线程池属性，具体有哪些可以参考 Hystrix 的官方文档
                    // 这里挑几个有代表性的
                    // 核心线程数量
                    @HystrixProperty(name = "coreSize", value = "1"),
                    /*
                      队列最大值
                      size > 0: 使用 LinkedBlockingQueue 来实现请求等待队列
                      默认 -1：SynchronousQueue 阻塞队列，不存储元素；简单说就是一个生产者消费者的例子，但是只有一个位置，给一个，就必须有一个消费完成后，才会有下一个位置
                        对于这种 JUC 的功能，最好还是自己去 debug 源码
                     */
                    @HystrixProperty(name = "maxQueueSize", value = "2"),
                    /*
                      队列大小拒绝阈值：在队列没有达到 maxQueueSize 值时，但是达到了这里的阀值则拒绝
                      在 maxQueueSize = -1 时无效
                     */
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "2"),
                    // 统计相关属性
                    // （线程池）统计窗口持续时间
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1024"),
                    // （线程池）窗口内桶的数量
                    // 这两个加起来的含义就是：在持续时间内，均分多少个桶
                    // 大概好像是：比如 10 分钟，10 个桶，那么随着时间的推移，到了 11 分钟，那么第 1 个桶就被丢弃，然后成为了第 10 个桶
                    // 在这 1 分钟内的数据都被存储在这个桶里面；总共就 10 个桶来回倒腾
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "2")
            },
            commandProperties = {
                    // 熔断降级相关属性也可以放到这里
            }
    )
    public CommonResult usernameIsExist(@RequestParam String username) {

        // 1. 判断用户名不能为空
        if (StringUtils.isBlank(username)) {
            return CommonResult.errorMsg("用户名不能为空");
        }

        // 2. 查找注册的用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return CommonResult.errorMsg("用户名已经存在");
        }

        // 3. 请求成功，用户名没有重复
        return CommonResult.ok();
    }

    @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "POST")
    @PostMapping("/regist")
    public CommonResult regist(@RequestBody UserBO userBO,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        if (userApplicationProperties.isDisabledRegistration()) {
            log.info("{} 该用户被系统拦截注册", userBO.getUsername());
            return CommonResult.errorMsg("当前注册用户过多，请稍后再试");
        }

        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPwd = userBO.getConfirmPassword();

        // 0. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password) ||
                StringUtils.isBlank(confirmPwd)) {
            return CommonResult.errorMsg("用户名或密码不能为空");
        }

        // 1. 查询用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return CommonResult.errorMsg("用户名已经存在");
        }

        // 2. 密码长度不能少于6位
        if (password.length() < 6) {
            return CommonResult.errorMsg("密码长度不能少于6");
        }

        // 3. 判断两次密码是否一致
        if (!password.equals(confirmPwd)) {
            return CommonResult.errorMsg("两次密码输入不一致");
        }

        // 4. 实现注册
        Users user = userService.createUser(userBO);

        // 生成用户token
        AuthResponse token = authService.tokenize(user.getId());
        if (!token.getCode().equals(AuthResponseCode.SUCCESS)) {
            log.error("token error uid ={}", user.getId());
            return CommonResult.errorMsg("token error");
        }
        // 将 token 信息添加到 响应的 header 中
        addAuth2Header(response, token.getAccount());

        // user = setNullProperty(user);
        UserVO userVO = conventUsersVO(user);
        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(userVO), true);


        // 同步购物车数据
        synchShopcartData(user.getId(), request, response);

        return CommonResult.ok();
    }

    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
    @PostMapping("/login")
    public CommonResult login(@RequestBody UserBO userBO,
                              HttpServletRequest request,
                              HttpServletResponse response) throws Exception {

        String username = userBO.getUsername();
        String password = userBO.getPassword();

        // 0. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password)) {
            return CommonResult.errorMsg("用户名或密码不能为空");
        }

        // 1. 实现登录
        Users user = userService.queryUserForLogin(username,
                MD5Utils.getMD5Str(password));

        if (user == null) {
            return CommonResult.errorMsg("用户名或密码不正确");
        }

        // 生成用户token
        AuthResponse token = authService.tokenize(user.getId());
        if (!token.getCode().equals(AuthResponseCode.SUCCESS)) {
            log.error("token error uid ={}", user.getId());
            return CommonResult.errorMsg("token error");
        }
        // 将 token 信息添加到 响应的 header 中
        addAuth2Header(response, token.getAccount());

        // user = setNullProperty(user);
        UserVO userVO = conventUsersVO(user);
        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(userVO), true);

        // TODO 生成用户token，存入redis会话
        // 同步购物车数据
        synchShopcartData(user.getId(), request, response);

        return CommonResult.ok(user);
    }

    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    public CommonResult logout(@RequestParam String userId,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        Account account = Account.builder()
                // 直接从请求头中获取这些信息，因为前面要求需要填写这些信息
                .token(request.getHeader(AUTH))
                .userId(userId)
                .refreshToken(request.getHeader(REFRESH_TOKEN_HEADER))
                .build();

        AuthResponse resp = authService.delete(account);
        if (!resp.getCode().equals(AuthResponseCode.SUCCESS)) {
            return CommonResult.errorMsg("token error");
        }

        // 清除用户的相关信息的cookie
        CookieUtils.deleteCookie(request, response, "user");

        // 用户退出登录，清除redis中user的会话信息
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);

        // 分布式会话中需要清除用户数据
        CookieUtils.deleteCookie(request, response, FOODIE_SHOPCART);

        return CommonResult.ok();
    }

    /**
     * 降级方法，原始方法有什么参数就必须有什么参数，但是可以多一个 Throwable 参数
     *      当异常降级的时候，就会把那个异常注入给你
     * @author Jiahao Wang
     * @date 2022/3/31 下午1:38
     * @param username username
     * @param throwable throwable
     * @return com.example.pojo.CommonResult
     */
    private CommonResult loginFail(@RequestParam String username,
                                   Throwable throwable) throws Exception {
        return CommonResult.errorMsg("验证码输错了（模仿 12306）");
    }

    /**
     * 注册登录成功后，同步cookie和redis中的购物车数据
     */
    private void synchShopcartData(String userId, HttpServletRequest request,
                                   HttpServletResponse response) {

        /**
         * 1. redis中无数据，如果cookie中的购物车为空，那么这个时候不做任何处理
         *                 如果cookie中的购物车不为空，此时直接放入redis中
         * 2. redis中有数据，如果cookie中的购物车为空，那么直接把redis的购物车覆盖本地cookie
         *                 如果cookie中的购物车不为空，
         *                      如果cookie中的某个商品在redis中存在，
         *                      则以cookie为主，删除redis中的，
         *                      把cookie中的商品直接覆盖redis中（参考京东）
         * 3. 同步到redis中去了以后，覆盖本地cookie购物车的数据，保证本地购物车的数据是同步最新的
         */

        // 从redis中获取购物车
        String shopcartJsonRedis = redisOperator.get(FOODIE_SHOPCART + ":" + userId);

        // 从cookie中获取购物车
        String shopcartStrCookie = CookieUtils.getCookieValue(request, FOODIE_SHOPCART, true);

        if (StringUtils.isBlank(shopcartJsonRedis)) {
            // redis为空，cookie不为空，直接把cookie中的数据放入redis
            if (StringUtils.isNotBlank(shopcartStrCookie)) {
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, shopcartStrCookie);
            }
        } else {
            // redis不为空，cookie不为空，合并cookie和redis中购物车的商品数据（同一商品则覆盖redis）
            if (StringUtils.isNotBlank(shopcartStrCookie)) {

                /**
                 * 1. 已经存在的，把cookie中对应的数量，覆盖redis（参考京东）
                 * 2. 该项商品标记为待删除，统一放入一个待删除的list
                 * 3. 从cookie中清理所有的待删除list
                 * 4. 合并redis和cookie中的数据
                 * 5. 更新到redis和cookie中
                 */

                List<ShopcartBO> shopcartListRedis = JsonUtils.jsonToList(shopcartJsonRedis, ShopcartBO.class);
                List<ShopcartBO> shopcartListCookie = JsonUtils.jsonToList(shopcartStrCookie, ShopcartBO.class);

                // 定义一个待删除list
                List<ShopcartBO> pendingDeleteList = new ArrayList<>();

                for (ShopcartBO redisShopcart : shopcartListRedis) {
                    String redisSpecId = redisShopcart.getSpecId();

                    for (ShopcartBO cookieShopcart : shopcartListCookie) {
                        String cookieSpecId = cookieShopcart.getSpecId();

                        if (redisSpecId.equals(cookieSpecId)) {
                            // 覆盖购买数量，不累加，参考京东
                            redisShopcart.setBuyCounts(cookieShopcart.getBuyCounts());
                            // 把cookieShopcart放入待删除列表，用于最后的删除与合并
                            pendingDeleteList.add(cookieShopcart);
                        }

                    }
                }

                // 从现有cookie中删除对应的覆盖过的商品数据
                shopcartListCookie.removeAll(pendingDeleteList);

                // 合并两个list
                shopcartListRedis.addAll(shopcartListCookie);
                // 更新到redis和cookie
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, JsonUtils.objectToJson(shopcartListRedis),
                        true);
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartListRedis));
            } else {
                // redis不为空，cookie为空，直接把redis覆盖cookie
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, shopcartJsonRedis, true);
            }

        }
    }

    /**
     * 将 token 信息添加到响应的 header 中，前端处理登录流程的地方，就要额外对照换个 header 进行处理，
     * 并按规范添加到 请求头中，在通过网关鉴权的时候才会通过
     *
     * @param response
     * @param token
     */
    private void addAuth2Header(HttpServletResponse response, Account token) {
        response.addHeader(AUTH, token.getToken());
        response.addHeader(REFRESH_TOKEN_HEADER, token.getRefreshToken());
        response.addHeader(USERNAME, token.getUserId());

        // 告诉前端 token 过期的时间，在过期前如果检测到用户当前还有操作的话，就刷新 token
        // 不过这里还需要注意的是：这里设置为过期时间为 1 天
        // 在 token 生成的时候， jwt 里面有一个 withExpiresAt 值，最好将这个也改成一样的
        // 不然如果依赖 jwt 里面的过期时间的话，就有异议了
        Calendar expTime = Calendar.getInstance();
        expTime.add(Calendar.DAY_OF_MONTH, 1);
        response.addHeader("token-exp-time", expTime.getTimeInMillis() + "");
    }

    /**
     * convert
     */
    public UserVO conventUsersVO(Users user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    private Users setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }
}
