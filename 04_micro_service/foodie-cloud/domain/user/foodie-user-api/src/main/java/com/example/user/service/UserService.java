package com.example.user.service;

import com.example.user.pojo.Users;
import com.example.user.pojo.bo.UserBO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "foodie-user-service", contextId = "user-api")
@RequestMapping("user-api")
public interface UserService {

    /**
     * 判断用户名是否存在
     */
    @GetMapping("user/exists")
    public boolean queryUsernameIsExist(@RequestParam("username") String username);

    /**
     * 判断用户名是否存在
     */
    @PostMapping("user")
    public Users createUser(@RequestBody UserBO userBO);

    /**
     * 检索用户名和密码是否匹配，用于登录
     */
    @GetMapping("verify")
    public Users queryUserForLogin(@RequestParam("username") String username,
                                   @RequestParam("password") String password);
}
