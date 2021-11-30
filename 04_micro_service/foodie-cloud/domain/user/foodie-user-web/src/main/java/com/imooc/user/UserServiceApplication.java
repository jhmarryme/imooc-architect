package com.imooc.user;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 *
 * @author JiaHao Wang
 * @date 2021/11/30 上午11:24
 */
@SpringBootApplication
// 扫描 mybatis 通用 mapper 所在的包
@MapperScan(basePackages = "com.example.user.mapper")
// 扫描所有包以及相关组件包
@ComponentScan(basePackages = {"com.example", "org.n3r.idworker"})
@EnableDiscoveryClient
// TODO feign注解
public class UserServiceApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(UserServiceApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
