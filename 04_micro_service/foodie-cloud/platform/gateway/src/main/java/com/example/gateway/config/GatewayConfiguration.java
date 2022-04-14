package com.example.gateway.config;

import com.example.gateway.filter.AuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author JiaHao Wang
 * @date 2022/4/12 下午5:18
 */
@Configuration
public class GatewayConfiguration {

    @Autowired
    private KeyResolver remoteAddressKeyResolver;

    @Autowired
    @Qualifier("redisRateLimiterUser")
    private RedisRateLimiter redisRateLimiterUser;
    //
    // @Autowired
    // private AuthFilter authFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder, AuthFilter authFilter) {
        return builder.routes()
                .route(
                        "foodie-user-service",
                        r -> r.path("/address/**", "/passport/**", "/center/**", "/userInfo/**")
                                .filters(
                                        f -> f
                                                // 应用配置了context-path, 为了不改变前端, 在这里加上对应路径
                                                .prefixPath("/foodie-user-service")
                                                // redis限流配置
                                                .requestRateLimiter(config -> {
                                                    config.setRateLimiter(redisRateLimiterUser);
                                                    config.setKeyResolver(remoteAddressKeyResolver);
                                                })
                                )
                                .uri("lb://foodie-user-service")
                ).route(
                        "foodie-order-service",
                        r -> r.path("/orders/**", "/mycomments/**", "/myorders/**")
                                .filters(f -> f.filter(authFilter)
                                        .prefixPath("/foodie-order-service"))
                                .uri("lb://foodie-order-service")
                ).route(
                        "foodie-cart-service",
                        r -> r.path("/shopcart/**")
                                .filters(f -> f.prefixPath("/foodie-cart-service"))
                                .uri("lb://foodie-cart-service")
                ).route(
                        "foodie-item-service",
                        r -> r.path("/items/**")
                                .filters(f -> f.prefixPath("/foodie-item-service"))
                                .uri("lb://foodie-item-service")
                ).route(
                        "foodie-auth-service",
                        r -> r.path("/foodie-auth-service/refreash/**")
                                .uri("lb://foodie-auth-service")
                )
                .build();
    }
}
