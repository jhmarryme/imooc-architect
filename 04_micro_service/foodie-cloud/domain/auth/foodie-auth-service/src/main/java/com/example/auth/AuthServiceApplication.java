package com.example.auth;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 *
 * @author JiaHao Wang
 * @date 2022/4/13 下午1:55
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AuthServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(AuthServiceApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
