package com.example;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

/**
 *
 * @author JiaHao Wang
 * @date 2021/12/16 上午9:30
 */
@EnableAutoConfiguration
@EnableDiscoveryClient
@EnableTurbine
public class HystrixTurbineApplication {
}
