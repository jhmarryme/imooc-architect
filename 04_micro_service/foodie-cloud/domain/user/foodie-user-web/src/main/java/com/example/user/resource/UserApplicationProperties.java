package com.example.user.resource;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
@Data
public class UserApplicationProperties {

    // 直接引用远程的属性名
    @Value("${userservice.registration.disabled}")
    private boolean disabledRegistration;
}
