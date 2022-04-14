package com.example.auth.service.impl;

import com.example.auth.service.AuthService;
import com.example.auth.service.entity.Account;
import com.example.auth.service.entity.AuthResponse;
import com.example.auth.service.entity.AuthResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 *
 * @author JiaHao Wang
 * @date 2022/4/13 下午12:55
 */
@Slf4j
@RestController
public class AuthServiceImpl implements AuthService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RedisTemplate redisTemplate;

    public static final String USER_TOKEN = "USER-TOKEN-";

    @Override
    public AuthResponse tokenize(String userId) {
        Account account = Account.builder()
                .userId(userId)
                .build();

        String token = jwtService.token(account);
        account.setToken(token);
        account.setRefreshToken(UUID.randomUUID().toString());

        // 还需要将这个 结果 信息存放到某个地方，后续刷新的时候才能拿到对应的账户信息
        redisTemplate.opsForValue().set(USER_TOKEN + account.getUserId(), account);
        redisTemplate.opsForValue().set(account.getRefreshToken(), account);

        return AuthResponse.builder()
                .account(account)
                .code(AuthResponseCode.SUCCESS)
                .build();
    }

    @Override
    public AuthResponse verify(Account account) {
        boolean success = jwtService.verify(account.getToken(), account.getUserId());
        return AuthResponse.builder()
                // TODO 此处最好用invalid token之类的错误信息
                .code(success ? AuthResponseCode.SUCCESS : AuthResponseCode.USER_NOT_FOUND)
                .build();
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        Account account = (Account) redisTemplate.opsForValue().get(refreshToken);
        if (account == null) {
            return AuthResponse.builder()
                    .code(AuthResponseCode.USER_NOT_FOUND)
                    .build();
        }

        // 删除老的 刷新 token
        redisTemplate.delete(refreshToken);
        // 删除老的 token
        redisTemplate.delete(account.getToken() + account.getUserId());

        String jwt = jwtService.token(account);
        account.setToken(jwt);
        account.setRefreshToken(UUID.randomUUID().toString());

        // 存入新的 token 信息
        redisTemplate.opsForValue().set(account.getToken() + account.getUserId(), account);
        redisTemplate.opsForValue().set(account.getRefreshToken(), account);

        return AuthResponse.builder()
                .account(account)
                .code(AuthResponseCode.SUCCESS)
                .build();
    }

    @Override
    public AuthResponse delete(Account account) {
        AuthResponse verify = verify(account);
        AuthResponse resp = new AuthResponse();

        if (verify.getCode().equals(AuthResponseCode.SUCCESS)) {
            // 删除登录时放进去的 token 信息
            redisTemplate.delete(USER_TOKEN + account.getUserId());
            redisTemplate.delete(account.getRefreshToken());
            resp.setCode(AuthResponseCode.SUCCESS);
        } else {
            resp.setCode(AuthResponseCode.USER_NOT_FOUND);
        }
        return resp;
    }
}
