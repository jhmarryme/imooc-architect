package com.example.gateway;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * redis限流配置
 * @author JiaHao Wang
 * @date 2022/4/13 上午11:09
 */
@Configuration
public class RedisLimiterConfig {

    /**
     * 创建限流的 key
     *      真实环境中，不同的模块会声明不同的 key
     *      使用 @Primary 是为了避免在自动装配的时候 注入一个 KeyResolver 导致不知道注入哪一个而报错
     */
    @Bean
    @Primary
    public KeyResolver remoteAddressKeyResolver() {
        return exchange -> Mono.just(
                Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress()
        );
    }

    @Bean("redisRateLimiterUser")
    @Primary
    public RedisRateLimiter redisRateLimiterUser() {
        // RedisRateLimiter 构造函数有很多个，支持传入 redisTemplete、redis 脚本之类的, 这里使用最简单的
        return new RedisRateLimiter(
                // 每秒发几个令牌
                1,
                // 令牌桶的容量
                2
        );
    }

    @Bean("redisRateLimiterItem")
    public RedisRateLimiter redisRateLimiterItem() {
        return new RedisRateLimiter(
                10,
                20
        );
    }
}
