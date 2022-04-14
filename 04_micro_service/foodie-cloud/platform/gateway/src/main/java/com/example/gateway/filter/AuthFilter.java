package com.example.gateway.filter;

import com.example.auth.service.AuthService;
import com.example.auth.service.entity.Account;
import com.example.auth.service.entity.AuthResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthFilter implements GatewayFilter, Ordered {

    /** token 使用的头 */
    private static final String AUTH = "Authorization";

    /** 用户名 使用的头 */
    private static final String USERNAME = "imooc-user-name";

    /** token的前缀部分 */
    public static final String TOKEN_PREFIX = "Bearer ";

    @Autowired
    private AuthService authService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Auth start");
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders header = request.getHeaders();
        // 1. 从header中提取token和username, 需要去掉多余的部分(Bearer)
        String token = StringUtils.substringAfter(header.getFirst(AUTH), TOKEN_PREFIX);
        String username = header.getFirst(USERNAME);

        ServerHttpResponse response = exchange.getResponse();
        if (StringUtils.isBlank(token)) {
            log.error("token not found");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // 2. 调用auth服务验证token
        Account account = Account.builder()
                .token(token)
                .userId(username)
                .build();
        AuthResponse resp = authService.verify(account);
        if (resp.getCode() != 1L) {
            log.error("invalid token");
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }

        // 3. 可以在header中存放用户信息
        // TODO 将用户信息存放在请求header中传递给下游业务
        ServerHttpRequest.Builder mutate = request.mutate();
        mutate.header("imooc-user-name", username);
        ServerHttpRequest buildReuqest = mutate.build();

        // 4. 可以在response中存放用户信息
        //todo 如果响应中需要放数据，也可以放在response的header中
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add("imooc-username", username);

        // 5. do filter
        return chain.filter(exchange.mutate()
                .request(buildReuqest)
                .response(response)
                .build());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
