package com.example.auth.service;

import com.example.auth.service.entity.Account;
import com.example.auth.service.entity.AuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author JiaHao Wang
 * @date 2022/4/13 下午12:51
 */
@FeignClient(name = "foodie-auth-service", contextId = "auth-api", path = "foodie-auth-service")
public interface AuthService {

    /**
     * token 发放; 请在发放 token 之前先校验 用户名和密码 是否正确
     *     对于 token 发放来说，在生产环境可以使用更高等级的加算法；
     *     比如非对称加密算法，使用一对密钥，用其中一个 key，比如使用私钥加密，其他服务端使用公钥解密
     *     优点：符合规范，节约一次 HTTP CALL，节约 10 毫秒左右的时间
     *
     * @param userId userId
     */
    @PostMapping("/token")
    @ResponseBody
    AuthResponse tokenize(@RequestParam("userId") String userId);

    /**
     * 验证
     *     对于验证来说：会下方到客户端去验证，而不是在 auth 层提供验证服务。
     *     比如网关层来说，这会延长服务耗时，因为多了一次 Http 调用
     *     由于这里验证使用的是  jwt 验证，不依赖其他第三方服务，可以很容易的拿到这个 密钥 进行验证
     *     这个是业界常用的做法
     *
     * @param account account
     */
    @PostMapping("/verify")
    @ResponseBody
    AuthResponse verify(@RequestBody Account account);

    /**
     * 发布 token 的时候会给定一个有效期，有效期到了之后，如果不想重新登录的话，
     * 就可以使用这个方法刷新 token，返回一个新的 token
     *
     * @param refreshToken refreshToken
     */
    @PostMapping("/refresh")
    @ResponseBody
    AuthResponse refresh(@RequestParam("refreshToken") String refreshToken);

    /**
     * 登出/注销, token
     *
     */
    @DeleteMapping("/delete")
    @ResponseBody
    AuthResponse delete(@RequestBody Account account);
}
