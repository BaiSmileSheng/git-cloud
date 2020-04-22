package com.cloud;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.cloud.system.annotation.EnableRyFeignClients;

import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableDiscoveryClient
@MapperScan("com.cloud.*.mapper")
@EnableRyFeignClients
public class CloudActApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudActApplication.class, args);
    }
}
