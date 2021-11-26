package com.jhmarryme.fdfs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.jhmarryme.mapper")
@ComponentScan(basePackages = {"com.jhmarryme", "org.n3r.idworker"})
public class FastDfsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FastDfsApplication.class, args);
    }

}
