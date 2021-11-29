package com.example.item;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 *
 * @author JiaHao Wang
 * @date 2021/11/29 下午3:40
 */
@SpringBootApplication
@EnableEurekaClient
// 扫描 mybatis 通用 mapper 所在的包
@MapperScan(basePackages = "com.example.item.mapper")
// 扫描所有包以及相关组件包
@ComponentScan(basePackages = {"com.example", "org.n3r.idworker"})
public class ItemServiceApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ItemServiceApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
