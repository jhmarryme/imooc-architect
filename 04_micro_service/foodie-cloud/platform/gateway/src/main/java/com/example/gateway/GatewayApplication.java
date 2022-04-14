package com.example.gateway;

import com.example.auth.service.AuthService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 *
 * @author JiaHao Wang
 * @date 2022/4/12 下午5:12
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackageClasses = {
        AuthService.class
})
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class);
    }
}
