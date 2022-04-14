package com.example.auth.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.auth.service.entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class JwtService {

    /** 密钥：生产环境下是外部传进来的, 可以采用配置中心加密方式传递进来 */
    private static final String KEY = "changeIt";

    /** 颁发者 */
    private static final String ISSUER = "www";

    /** 过期时间：毫秒 */
    private static final long TOKEN_EXP_TIME = 600000;

    /** 用户名 */
    private static final String USER_NAME = "username";

    /**
     * 生成Token, 登录完成后，调用该方法
     *
     * @param acct
     * @return
     */
    public String token(Account acct) {
        Date now = new Date();
        Algorithm algorithm = Algorithm.HMAC256(KEY);

        String token = JWT.create()
                // 颁发者
                .withIssuer(ISSUER)
                // 颁发时间
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + TOKEN_EXP_TIME))
                // 追加自定义声明
                .withClaim(USER_NAME, acct.getUserId())
//                .withClaim("ROLE", "")
                .sign(algorithm);

        log.info("jwt generated user={}", acct.getUserId());
        return token;
    }

    /**
     * 校验Token
     *
     * @param token
     * @param username
     * @return
     */
    public boolean verify(String token, String username) {
        log.info("verifying jwt - username={}", username);

        try {
            Algorithm algorithm = Algorithm.HMAC256(KEY);
            // 构建验证器，使用颁发的时候同一个算法
            JWTVerifier verifier = JWT.require(algorithm)
                    // 这里可以验证颁发的时候放进去的所有信息
                    .withIssuer(ISSUER)
                    .withClaim(USER_NAME, username)
                    .build();

            verifier.verify(token);
            return true;
        } catch (Exception e) {
            log.error("auth failed", e);
            return false;
        }

    }

}
