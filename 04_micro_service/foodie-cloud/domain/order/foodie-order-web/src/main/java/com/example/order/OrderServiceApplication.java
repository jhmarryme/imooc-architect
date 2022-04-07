package com.example.order;

import com.example.order.service.feign.ItemCommentsFeignClient;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 *
 * @author JiaHao Wang
 * @date 2021/11/30 下午12:31
 */
@SpringBootApplication
@MapperScan(basePackages = "com.example.order.mapper")
@ComponentScan(basePackages = {"com.example", "org.n3r.idworker"})
@EnableEurekaClient
@EnableFeignClients(
        basePackages = {
                "com.example.user.service",
                "com.example.item.service",
                "com.example.order.service.feign"
        }
)
public class OrderServiceApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(OrderServiceApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
