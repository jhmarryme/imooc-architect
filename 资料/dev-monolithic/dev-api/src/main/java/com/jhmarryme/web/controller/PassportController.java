package com.jhmarryme.web.controller;

import com.jhmarryme.pojo.Users;
import com.jhmarryme.pojo.bo.UserBO;
import com.jhmarryme.service.UsersService;
import com.jhmarryme.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * description: 
 * @author JiaHao Wang
 * @date 2021/1/26 22:58
 */
@Api(tags = {"用于注册登录的接口"}, value = "注册登录")
@RestController
@RequestMapping("/passport")
public class PassportController {

    @Autowired
    private UsersService usersService;

    @ApiOperation(value = "判断用户名是否存在", notes = "判断用户名是否存在", httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public Object usernameIsExist(@RequestParam String username) {

        if (StringUtils.isBlank(username)) {
            return CommonResult.errorMsg("用户名不能为空");
        }

        boolean isExist = usersService.queryUsernameIsExist(username);
        if (isExist) {
            return CommonResult.errorMsg("用户名已存在");
        }
        return CommonResult.ok();
    }
    @ApiOperation(value = "注册用户", notes = "注册", httpMethod = "POST")
    @PostMapping("/regist")
    public CommonResult regist(@RequestBody UserBO userBO,
                               HttpServletRequest request,
                               HttpServletResponse response) {

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
        boolean isExist = usersService.queryUsernameIsExist(username);
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
        Users userResult = usersService.createUser(userBO);
        CookieUtils.setCookie(request, response, "user", JsonUtil.objectToJson(setNullProperty(userResult)), true);

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
        Users userResult = usersService.queryUserForLogin(username,
                MD5Utils.getMD5Str(password));

        if (userResult == null) {
            return CommonResult.errorMsg("用户名或密码不正确");
        }

        CookieUtils.setCookie(request, response, "user", JsonUtil.objectToJson(setNullProperty(userResult)), true);

        return CommonResult.ok(userResult);
    }

    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    public CommonResult logout(@RequestParam String userId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {

        // 清除用户的相关信息的cookie
        CookieUtils.deleteCookie(request, response, "user");

        // TODO 用户退出登录，需要清空购物车
        // TODO 分布式会话中需要清除用户数据

        return CommonResult.ok();
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
