package com.cloud.generator;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.cloud.system.annotation.EnableRyFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.cloud.*.mapper")
@EnableRyFeignClients
public class CloudGenApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudGenApplication.class, args);
    }
}
